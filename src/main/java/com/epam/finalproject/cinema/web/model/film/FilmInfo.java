package com.epam.finalproject.cinema.web.model.film;

import com.epam.finalproject.cinema.domain.film.Film;
import com.epam.finalproject.cinema.domain.Genre.Genre;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "FilmInfo{" +
                "film=" + film +
                ", genres=" + genres +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmInfo filmInfo = (FilmInfo) o;
        return Objects.equals(film, filmInfo.film) && Objects.equals(genres, filmInfo.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(film, genres);
    }
}
