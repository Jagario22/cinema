package com.epam.finalproject.cinema.domain.Genre;
/**
 * Constants with PostgresSQL queries of genre entity.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class GenreQuery {
    public final static String INSERT_INTO_GENRES_VALUES = "insert into genres (name) values (?)";
    public final static String SELECT_ALL_GENRES_OF_FILM = "select * from genres inner join genres_films gf on genres.id = " +
            "gf.genre_id inner join films f on f.id = gf.film_id where f.id = ?";
    public final static String SELECT_ALL_GENRES = "select * from genres";
}
