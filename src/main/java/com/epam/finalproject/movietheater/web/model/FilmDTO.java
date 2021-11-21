package com.epam.finalproject.movietheater.web.model;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.Genre;
import com.epam.finalproject.movietheater.domain.entity.Session;

import java.util.List;

public class FilmDTO {
    private Film film;
    private List<Genre> genres;

    public FilmDTO(Film film, List<Genre> genres) {
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
