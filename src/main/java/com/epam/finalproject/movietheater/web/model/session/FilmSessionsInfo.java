package com.epam.finalproject.movietheater.web.model.session;

import com.epam.finalproject.movietheater.domain.entity.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilmSessionsInfo {
    private LocalDate date;
    private List<SessionPlacesInfo> sessionsPlacesInfo;
    private int filmId;
    private Session.Lang lang;

    public FilmSessionsInfo(LocalDate localDate, int filmId, Session.Lang lang) {
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

    public List<SessionPlacesInfo> getSessionsPlacesInfo() {
        if (sessionsPlacesInfo == null) {
            setSessionsPlacesInfo(new ArrayList<>());
        }
        return sessionsPlacesInfo;
    }

    public void setSessionsPlacesInfo(List<SessionPlacesInfo> sessionsPlacesInfo) {
        this.sessionsPlacesInfo = sessionsPlacesInfo;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
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
        FilmSessionsInfo that = (FilmSessionsInfo) o;
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
                ", sessionsPlacesInfo=" + sessionsPlacesInfo +
                ", filmId=" + filmId +
                ", lang=" + lang +
                '}';
    }
}
