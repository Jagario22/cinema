package com.epam.finalproject.cinema.domain.film;

/**
 * Constants with PostgresSQL queries of film entity.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class FilmQuery {
    public final static String SELECT_CURRENT_FILM_COUNT_ORDER_BY_PLACES_WHERE_DATETIME_AFTER =
            "select count (*) from (select f.* as places_count from films as f inner join sessions s on f.id = s.film_id " +
                    "inner join tickets t on s.id = t.session_id where (t.user_id is null and s.date_time > ?) group by f.id) as f_count";
    public final static String SELECT_CURRENT_FILM_COUNT_ORDER_BY_PLACES_WHERE_DATETIME_BETWEEN =
            "select count (*) from(select f.* as places_count from films as f inner join sessions s on f.id = s.film_id " +
                    "inner join tickets t on s.id = t.session_id where (t.user_id is null and s.date_time between ? and ?) group by f.id)  as f_count";
    public final static String SELECT_ALL_CURRENT_FILMS_COUNT_WHERE_DATETIME_AFTER = "select count (*) from (select distinct f.* from films as f " +
            "inner join sessions s on f.id = s.film_id " +
            "where s.date_time > ?) as f_count";
    public final static String SELECT_ALL_CURRENT_FILMS_COUNT_WHERE_DATETIME_BETWEEN = "select count (*) from (select distinct f.* from films as f " +
            "inner join sessions s on f.id = s.film_id " +
            "where s.date_time between ? and ?) as f_count";
    public final static String SELECT_ALL_CURRENT_FILMS_WHERE_DATETIME_AFTER = "select distinct f.*, min(%s) as min_f " +
            "from films as f inner join sessions s on f.id = s.film_id where s.date_time > ? " +
            "group by f.id, title, len, year_prod, category, descr, rating, img, last_showing_date " +
            "ORDER BY min_f offset(?) limit(?)";
    public final static String SELECT_ALL_CURRENT_FILMS_WHERE_DATETIME_BETWEEN = "select distinct f.*, min(%s) as min_f " +
            "from films as f inner join sessions s on f.id = s.film_id where s.date_time between ? and ? " +
            "group by f.id, title, len, year_prod, category, descr, rating, img, last_showing_date " +
            "ORDER BY min_f offset(?) limit(?)";
    public final static String SELECT_CURRENT_FILM_ORDER_BY_PLACES_WHERE_DATETIME_AFTER =
            "select f.*, count(t.id) as places_count from films as f inner join sessions s on f.id = s.film_id " +
                    "inner join tickets t on s.id = t.session_id where (t.user_id is null and s.date_time > ?) group by f.id order by " +
                    "places_count desc offset(?) limit(?)";
    public final static String SELECT_CURRENT_FILM_ORDER_BY_PLACES_WHERE_DATETIME_BETWEEN =
            "select f.*, count(t.id) as places_count from films as f inner join sessions s on f.id = s.film_id " +
                    "inner join tickets t on s.id = t.session_id where (t.user_id is null and s.date_time between ? and ?) group by f.id order by " +
                    "places_count desc offset(?) limit(?)";
    public final static String INSERT_INTO_FILMS_VALUES = "insert into films (title, len, year_prod, category, descr, rating, " +
            "img, last_showing_date) values (?, ?, ?, ?, ?, ?, ?, ?)";
    public final static String INSERT_INTO_GENRES_FILMS = "insert into genres_films (film_id, genre_id) values (?, ?)";
    public final static String SELECT_FILM_BY_ID = "select * from films where id = ?";

    public final static String SELECT_ALL_FILMS = "select * from films";
    public static final String FILM_TITLE_FIELD = "f.title";




}
