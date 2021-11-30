package com.epam.finalproject.cinema.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Session implements Serializable {
    private static final long serialVersionUID = -9122858044519977795L;
    private int id;
    private int filmId;
    private LocalDateTime date;
    private Lang lang;

    public Session(int id, int filmId, LocalDateTime date, Lang lang) {
        this.id = id;
        this.filmId = filmId;
        this.date = date;
        this.lang = lang;
    }

    public enum Lang {
        UKRAINIAN,
        ORIGINAL
    }

    public Session(Integer id, Integer filmId, LocalDateTime date, Lang lang) {
        this.id = id;
        this.filmId = filmId;
        this.date = date;
        this.lang = lang;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public LocalDateTime getLocaleDateTime() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", filmId=" + filmId +
                ", date=" + date +
                ", lang=" + lang +
                '}';
    }
}
