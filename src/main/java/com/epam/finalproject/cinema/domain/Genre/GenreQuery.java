package com.epam.finalproject.cinema.domain.Genre;

public class GenreQuery {
    public final static String INSERT_INTO_GENRES_VALUES = "insert into genres (name) values (?)";
    public final static String SELECT_ALL_GENRES_OF_FILM = "select * from genres inner join genres_films gf on genres.id = " +
            "gf.genre_id inner join films f on f.id = gf.film_id where f.id = ?";
    public final static String SELECT_ALL_GENRES = "select * from genres";
}
