package com.epam.finalproject.movietheater.service;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.dao.SessionDao;
import com.epam.finalproject.movietheater.domain.dao.TicketDao;
import com.epam.finalproject.movietheater.domain.dao.TicketTypeDao;
import com.epam.finalproject.movietheater.domain.dao.WalletDao;
import com.epam.finalproject.movietheater.domain.entity.Session;
import com.epam.finalproject.movietheater.domain.entity.Ticket;
import com.epam.finalproject.movietheater.domain.entity.TicketType;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.domain.exception.purchase.InactiveFilmSessionException;
import com.epam.finalproject.movietheater.domain.exception.purchase.InsufficientBalanceException;
import com.epam.finalproject.movietheater.domain.exception.purchase.TicketIsNotAvailableException;
import com.epam.finalproject.movietheater.domain.exception.purchase.TicketPurchaseException;
import com.epam.finalproject.movietheater.web.model.TicketDTO;
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
    private final SessionDao sessionDao;
    private final WalletDao walletDao;
    private final TicketTypeDao ticketTypeDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public TicketService() {
        ticketDao = TicketDao.getInstance();
        ticketTypeDao = TicketTypeDao.getInstance();
        walletDao = WalletDao.getInstance();
        sessionDao = SessionDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }

    public static synchronized TicketService getInstance() {
        if (instance == null) {
            instance = new TicketService();
        }
        return instance;
    }

    public synchronized void processTicketPurchase(int ticketId, int sessionId, int userId) throws DBException, IllegalStateException,
            InactiveFilmSessionException, InsufficientBalanceException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            Session session = sessionDao.findCurrentSessionById(sessionId, connection);
            if (session != null) {
                Ticket ticket = ticketDao.findTicketByIdWhereUserIdIsNull(ticketId, connection);
                if (ticket != null) {
                    BigDecimal price = getTicketPrice(ticket, connection);
                    BigDecimal userBalance = walletDao.findBalanceByUserId(userId, connection);
                    moneyChargeOffByUserId(userId, price, userBalance, ticketId, connection);
                    updatingTicket(connection, ticketId, userId);
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
        } catch (SQLException | NamingException | TicketPurchaseException e) {
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

    public List<TicketDTO> getTicketsBySessionId(int sessionId) throws DBException {
        List<TicketDTO> ticketDTOList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Ticket> tickets = ticketDao.findTicketsBySessionId(sessionId, connection);

            for (Ticket ticket : tickets) {
                TicketType ticketType = ticketTypeDao.findById(ticket.getTicketTypeId(), connection);
                TicketDTO ticketDTO = new TicketDTO(ticket.getId(), ticket.getNumber(), ticketType);
                ticketDTOList.add(ticketDTO);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting tickets failed";
            processException(e, msg);
        } finally {
            String errorMsg = "Getting tickets failed";
            closeConnection(connection, errorMsg);
        }
        return ticketDTOList;
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
        connection.commit();
        log.debug("ticket updating was succeed, userId: " + userId + " ticketId" + ticketId);
    }

    private BigDecimal getTicketPrice(Ticket ticket, Connection connection) throws SQLException {
        return ticketTypeDao.findById(ticket.getId(), connection).getPrice();
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


    private void processException(Exception e, String msg) throws DBException {
        log.error(msg + "\n" + e.getMessage());
        throw new DBException(msg, e);
    }

}
