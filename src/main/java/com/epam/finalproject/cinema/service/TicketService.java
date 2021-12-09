package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.ticket.TicketDao;
import com.epam.finalproject.cinema.domain.ticket.type.TicketTypeDao;
import com.epam.finalproject.cinema.domain.wallet.WalletDao;
import com.epam.finalproject.cinema.domain.session.Session;
import com.epam.finalproject.cinema.domain.ticket.Ticket;
import com.epam.finalproject.cinema.domain.ticket.type.TicketType;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.exception.purchase.InactiveFilmSessionException;
import com.epam.finalproject.cinema.exception.purchase.InsufficientBalanceException;
import com.epam.finalproject.cinema.exception.purchase.TicketIsNotAvailableException;
import com.epam.finalproject.cinema.exception.purchase.TicketPurchaseException;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import com.epam.finalproject.cinema.web.model.ticket.TicketInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketService {
    private static TicketService instance = null;
    private final TicketDao ticketDao = TicketDao.getInstance();
    private final SessionService sessionService = SessionService.getInstance();
    private final WalletDao walletDao = WalletDao.getInstance();
    private final TicketTypeDao ticketTypeDao = TicketTypeDao.getInstance();
    private final ConnectionPool connectionPool = PostgresConnectionPool.getInstance();
    private final static Logger log = LogManager.getLogger(TicketService.class);

    public TicketService() {
    }

    public static synchronized TicketService getInstance() {
        if (instance == null) {
            instance = new TicketService();
        }
        return instance;
    }

    public List<TicketInfo> getTicketsInfoByUserId(int userId) throws DBException {
        Connection connection = null;
        List<TicketInfo> ticketInfoList = new ArrayList<>();

        try {
            connection = connectionPool.getConnection();
            List<Ticket> tickets = ticketDao.findTicketsByUserId(userId, connection);
            for (Ticket ticket : tickets) {
                SessionInfo sessionInfo = sessionService.getCurrentSessionInfoById(ticket.getSessionId(), connection);
                TicketInfo ticketInfo = getTicketSessionInfo(connection, ticket, sessionInfo);
                ticketInfoList.add(ticketInfo);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting ticket info of user";
            connectionRollback(connection, msg);
            log.debug(msg + "userId: " + userId + " failed.");
            throw new DBException(msg, e);
        } finally {
            String errorMsg = "Getting ticket info of user failed.";
            closeConnection(connection, errorMsg);
        }

        return ticketInfoList;
    }


    public synchronized void processTicketPurchase(int ticketId, int sessionId, int userId) throws DBException, IllegalStateException,
            InactiveFilmSessionException, InsufficientBalanceException, TicketPurchaseException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            Session session = sessionService.getCurrentSessionById(sessionId, connection);
            if (session == null) {
                sessionIsInactive(ticketId, sessionId, connection);
                return;
            }
            Ticket ticket = ticketDao.findTicketByIdWhereUserIdIsNull(ticketId, connection);
            if (ticket == null) {
                ticketIsNotAvailable(ticketId, sessionId, connection);
                return;
            }
            BigDecimal price = getTicketPrice(ticket, connection);
            BigDecimal userBalance = walletDao.findBalanceByUserId(userId, connection);
            moneyChargeOffByUserId(userId, price, userBalance, ticketId, connection);
            updatingTicket(connection, ticketId, userId);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Ticket purchase failed.";
            connectionRollback(connection, msg);
            log.error(msg + " ticketId: " + ticketId + " sessionId: " + sessionId + " userId: " + userId);
        } finally {
            String errorMsg = "Ticket purchase failed.";
            log.error(errorMsg);
            closeConnection(connection, errorMsg);
        }
    }

    private void ticketIsNotAvailable(int ticketId, int sessionId, Connection connection) throws SQLException, TicketIsNotAvailableException {
        String msg = "Ticket purchase failed. Ticket is not available for purchase";
        log.debug(msg + ", ticketId: " + ticketId + " sessionId: " + sessionId);
        connection.rollback();
        throw new TicketIsNotAvailableException(msg);
    }

    private void sessionIsInactive(int ticketId, int sessionId, Connection connection) throws DBException, InactiveFilmSessionException {
        String msg = "Ticket purchase failed. The film session is inactive";
        connectionRollback(connection, msg);
        log.debug(msg + ", ticketId: " + ticketId + " sessionId: " + sessionId);
        throw new InactiveFilmSessionException(msg);
    }

    public List<TicketInfo> getTicketsInfoBySessionIdWhereUserIsNull(int sessionId) throws DBException {
        List<TicketInfo> ticketInfoList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Ticket> tickets = ticketDao.findTicketsBySessionIdWhereUserIsNull(sessionId, connection);
            for (Ticket ticket : tickets) {
                TicketInfo ticketInfo = getTicketSessionInfo(connection, ticket);
                ticketInfoList.add(ticketInfo);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting tickets failed";
            connectionRollback(connection, msg);
            log.error(msg + " sessionId " + sessionId);
        } finally {
            String errorMsg = "Getting tickets failed";
            closeConnection(connection, errorMsg);
        }
        return ticketInfoList;
    }

    public List<TicketInfo> getTicketsInfoBySessionId(int sessionId, Connection connection) throws DBException, SQLException {
        List<TicketInfo> ticketInfoList = new ArrayList<>();
        try {
            List<Ticket> tickets = ticketDao.findAllTicketsOfSession(sessionId, connection);
            for (Ticket ticket : tickets) {
                SessionInfo sessionInfo = sessionService.getCurrentSessionInfoById(ticket.getSessionId(), connection);
                TicketInfo ticketInfo = getTicketSessionInfo(connection, ticket, sessionInfo);
                ticketInfoList.add(ticketInfo);
            }
        } catch (SQLException e) {
            String msg = "Getting tickets failed";
            log.error(msg + "sessionId" + sessionId);
            throw e;
        }
        return ticketInfoList;
    }

    private void updatingTicket(Connection connection, int ticketId, int userId) throws SQLException, TicketPurchaseException {
        ticketDao.updateTicketOnUserIdById(ticketId, userId, connection);
        log.debug("ticket updating was succeed, userId: " + userId + " ticketId" + ticketId);
    }

    private BigDecimal getTicketPrice(Ticket ticket, Connection connection) throws SQLException {
        return ticketTypeDao.findById(ticket.getTicketTypeId(), connection).getPrice();
    }

    private void moneyChargeOffByUserId(int userId, BigDecimal price,
                                        BigDecimal userBalance, int ticketId, Connection connection) throws SQLException, InsufficientBalanceException, DBException {
        if (price.compareTo(userBalance) <= 0) {
            BigDecimal newBalance = userBalance.subtract(price);
            walletDao.updateOnBalanceByUserId(userId, newBalance, connection);
        } else {
            String msg = "Ticket purchase failed. There are not enough funds on the balance sheet";
            log.debug(msg + ", ticketId: " + ticketId + " balance: " + userBalance +
                    ", price: " + price);
            throw new InsufficientBalanceException(msg);
        }
    }

    private TicketInfo getTicketSessionInfo(Connection connection, Ticket ticket) throws SQLException {
        TicketType ticketType = ticketTypeDao.findById(ticket.getTicketTypeId(), connection);
        return new TicketInfo(ticket.getId(), ticket.getNumber(), ticketType, ticket.getUserId());
    }

    private TicketInfo getTicketSessionInfo(Connection connection, Ticket ticket, SessionInfo sessionInfo) throws SQLException {
        TicketType ticketType = ticketTypeDao.findById(ticket.getTicketTypeId(), connection);
        return new TicketInfo(ticket.getId(), ticket.getNumber(), ticketType, sessionInfo, ticket.getUserId());
    }

    private void closeConnection(Connection connection, String msgError) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(msgError + "\n" + e.getMessage());
        }
    }

    private void connectionRollback(Connection connection, String errorMsg) throws DBException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DBException(errorMsg, e);
        }
    }

}
