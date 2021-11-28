package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.dao.TicketDao;
import com.epam.finalproject.cinema.domain.dao.TicketTypeDao;
import com.epam.finalproject.cinema.domain.dao.WalletDao;
import com.epam.finalproject.cinema.domain.entity.Session;
import com.epam.finalproject.cinema.domain.entity.Ticket;
import com.epam.finalproject.cinema.domain.entity.TicketType;
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
    private final TicketDao ticketDao;
    private final SessionService sessionService;
    private final WalletDao walletDao;
    private final TicketTypeDao ticketTypeDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(TicketService.class);

    public TicketService() {
        ticketDao = TicketDao.getInstance();
        sessionService = SessionService.getInstance();
        ticketTypeDao = TicketTypeDao.getInstance();
        walletDao = WalletDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
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
                TicketInfo ticketInfo = getTicketInfo(connection, ticket, sessionInfo);
                ticketInfoList.add(ticketInfo);
            }
        } catch (SQLException | NamingException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                processException(e, "Getting ticket info of user failed.");
            }
            processException(e, "Getting ticket info of user failed.");
        } finally {
            String errorMsg = "Ticket purchase failed.";
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
            if (session != null) {
                Ticket ticket = ticketDao.findTicketByIdWhereUserIdIsNull(ticketId, connection);
                if (ticket != null) {
                    BigDecimal price = getTicketPrice(ticket, connection);
                    BigDecimal userBalance = walletDao.findBalanceByUserId(userId, connection);
                    moneyChargeOffByUserId(userId, price, userBalance, ticketId, connection);
                    updatingTicket(connection, ticketId, userId);
                    connection.commit();
                } else {
                    String msg = "Ticket purchase failed. Ticket is not available for purchase";
                    log.debug(msg + ", ticketId: " + ticketId + " sessionId: " + sessionId);
                    connection.rollback();
                    throw new TicketIsNotAvailableException(msg);
                }
            } else {
                String msg = "Ticket purchase failed. The film session is inactive";
                log.debug(msg + ", ticketId: " + ticketId + " sessionId: " + sessionId);
                connection.rollback();
                throw new InactiveFilmSessionException(msg);
            }

        } catch (SQLException | NamingException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    processException(e, "Ticket purchase failed.");
                }
            }
            processException(e, "Ticket purchase failed.");
        } finally {
            String errorMsg = "Ticket purchase failed.";
            closeConnection(connection, errorMsg);
        }
    }

    public List<TicketInfo> getTicketsInfoBySessionId(int sessionId) throws DBException {
        List<TicketInfo> ticketInfoList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Ticket> tickets = ticketDao.findTicketsBySessionId(sessionId, connection);
            for (Ticket ticket : tickets) {
                TicketInfo ticketInfo = getTicketInfo(connection, ticket);
                ticketInfoList.add(ticketInfo);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting tickets failed";
            processException(e, msg);
        } finally {
            String errorMsg = "Getting tickets failed";
            closeConnection(connection, errorMsg);
        }
        return ticketInfoList;
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

    private void updatingTicket(Connection connection, int ticketId, int userId) throws SQLException, TicketPurchaseException {
        ticketDao.updateTicketOnUserIdById(ticketId, userId, connection);
        log.debug("ticket updating was succeed, userId: " + userId + " ticketId" + ticketId);
    }

    private BigDecimal getTicketPrice(Ticket ticket, Connection connection) throws SQLException {
        return ticketTypeDao.findById(ticket.getTicketTypeId(), connection).getPrice();
    }

    private void moneyChargeOffByUserId(int userId, BigDecimal price,
                                        BigDecimal userBalance, int ticketId, Connection connection) throws SQLException, InsufficientBalanceException {
        if (price.compareTo(userBalance) <= 0) {
            BigDecimal newBalance = userBalance.subtract(price);
            walletDao.updateOnBalanceByUserId(userId, newBalance, connection);
        } else {
            String msg = "Ticket purchase failed. There are not enough funds on the balance sheet";
            log.debug(msg + ", ticketId: " + ticketId + " balance: " + userBalance +
                    ", price: " + price);
            connection.rollback();
            throw new InsufficientBalanceException(msg);
        }
    }

    private TicketInfo getTicketInfo(Connection connection, Ticket ticket) throws SQLException {
        TicketType ticketType = ticketTypeDao.findById(ticket.getTicketTypeId(), connection);
        return new TicketInfo(ticket.getId(), ticket.getNumber(), ticketType);
    }

    private TicketInfo getTicketInfo(Connection connection, Ticket ticket, SessionInfo sessionInfo) throws SQLException {
        TicketType ticketType = ticketTypeDao.findById(ticket.getTicketTypeId(), connection);
        return new TicketInfo(ticket.getId(), ticket.getNumber(), ticketType, sessionInfo);
    }

    private void processException(Exception e, String msg) throws DBException {
        log.error(msg + "\n" + e.getMessage());
        throw new DBException(msg, e);
    }

}
