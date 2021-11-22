package com.epam.finalproject.movietheater.web.model;

import com.epam.finalproject.movietheater.domain.entity.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class FilmSessionsDTO {
    private LocalDate date;
    private List<LocalTime> timeList;
    private int filmId;
    private Session.Lang lang;

    public FilmSessionsDTO(LocalDate localDate, int filmId, Session.Lang lang) {
        this.date = localDate;
        this.filmId = filmId;
        this.lang = lang;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<LocalTime> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<LocalTime> timeList) {
        this.timeList = timeList;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public Session.Lang getLang() {
        return lang;
    }

    public void setLang(Session.Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmSessionsDTO that = (FilmSessionsDTO) o;
        return filmId == that.filmId && Objects.equals(date, that.date) && lang == that.lang;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, filmId, lang);
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "date=" + date +
                ", timeList=" + timeList +
                ", filmId=" + filmId +
                ", lang=" + lang +
                '}';
    }
}
