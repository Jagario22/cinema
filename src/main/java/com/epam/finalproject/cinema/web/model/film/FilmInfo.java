package com.epam.finalproject.cinema.web.model.film;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Genre;

import java.io.Serializable;
import java.util.List;

public class FilmInfo implements Serializable {
    private Film film;
    private List<Genre> genres;

    public FilmInfo(Film film, List<Genre> genres) {
        this.film = film;
        this.genres = genres;
    }


    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
