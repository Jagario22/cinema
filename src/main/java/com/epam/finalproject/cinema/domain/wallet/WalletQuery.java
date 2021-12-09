package com.epam.finalproject.cinema.domain.wallet;

public class WalletQuery {

    public final static String SELECT_WALLET_BALANCE_BY_USER_ID = "select balance from wallets where user_id=?";
    public final static String UPDATE_WALLET_ON_BALANCE_BY_USER_ID = "update wallets set balance=? where user_id=? ";

    public final static String SELECT_TICKET_COUNT_OF_FILM_WHERE_USER_IS_NULL_GROUP_BY_SESSION_ID =
            "select session_id,count(*) from (select * from films inner join sessions s on films.id = s.film_id " +
                    "inner join tickets t on s.id = t.session_id " +
                    "where film_id = ? and t.user_id is null) as c group by session_id";
    public final static String SELECT_WALLET_BY_USER_ID = "select * from wallets where user_id=?";

    public final static String INSERT_WALLET = "insert into wallets(user_id, balance) values(?, ?)";
}
