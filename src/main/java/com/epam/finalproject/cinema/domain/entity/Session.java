package com.epam.finalproject.cinema.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Session implements Serializable {
    private static final long serialVersionUID = -9122858044519977795L;
    private int id;
    private int filmId;
    private LocalDateTime dateTime;
    private Lang lang;

    public Session(int id, int filmId, LocalDateTime dateTime, Lang lang) {
        this.id = id;
        this.filmId = filmId;
        this.dateTime = dateTime;
        this.lang = lang;
    }

    public Session(LocalDate date, LocalTime time, int filmId, Lang lang) {
        this.dateTime = LocalDateTime.of(date, time);
        this.lang = lang;
        this.filmId = filmId;
    }

    public enum Lang {
        UKRAINIAN,
        ORIGINAL
    }

    public Session(Integer id, Integer filmId, LocalDateTime dateTime, Lang lang) {
        this.id = id;
        this.filmId = filmId;
        this.dateTime = dateTime;
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

    public LocalDateTime getLocalDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
                ", date=" + dateTime +
                ", lang=" + lang +
                '}';
    }
}
