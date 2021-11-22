package com.epam.finalproject.movietheater.web.model;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.Session;

import java.time.LocalDateTime;

public class SessionDTO {
    private int id;
    private Film film;
    private LocalDateTime dateTime;
    private Session.Lang lang;

    public SessionDTO(int id, Film film, LocalDateTime dateTime, Session.Lang lang) {
        this.id = id;
        this.film = film;
        this.dateTime = dateTime;
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Session.Lang getLang() {
        return lang;
    }

    public void setLang(Session.Lang lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "id=" + id +
                ", film=" + film +
                ", dateTime=" + dateTime +
                ", lang=" + lang +
                '}';
    }
}
