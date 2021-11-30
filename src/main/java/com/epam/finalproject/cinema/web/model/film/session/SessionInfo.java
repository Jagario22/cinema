package com.epam.finalproject.cinema.web.model.film.session;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class SessionInfo {
    private int id;
    private Film film;
    private LocalTime time;
    private LocalDate date;
    private Session.Lang lang;
    private int freePlacesCount;
    private int boughtTicketsCount;
    private int countOfTickets;

    public SessionInfo(int id, Film film, LocalDateTime dateTime, Session.Lang lang) {
        this.id = id;
        this.film = film;
        this.time = dateTime.toLocalTime();
        this.date = dateTime.toLocalDate();
        this.lang = lang;
    }

    public SessionInfo(int id, int freePlacesCount, LocalTime time) {
        this.id = id;
        this.freePlacesCount = freePlacesCount;
        this.time = time;
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Session.Lang getLang() {
        return lang;
    }

    public void setLang(Session.Lang lang) {
        this.lang = lang;
    }

    public int getFreePlacesCount() {
        return freePlacesCount;
    }

    public void setFreePlacesCount(int freePlacesCount) {
        this.freePlacesCount = freePlacesCount;
    }

    public int getBoughtTicketsCount() {
        return boughtTicketsCount;
    }

    public void setBoughtTicketsCount(int boughtTicketsCount) {
        this.boughtTicketsCount = boughtTicketsCount;
    }

    public boolean isEqualToSession(Session session) {
        return this.id == session.getId() && this.date.equals(session.getLocaleDateTime().toLocalDate()) &&
                this.time.equals(session.getLocaleDateTime().toLocalTime())
                && this.film.getId() == session.getFilmId();
    }

    public int getCountOfTickets() {
        return countOfTickets;
    }

    public void setCountOfTickets(int countOfTickets) {
        this.countOfTickets = countOfTickets;
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "id=" + id +
                ", film=" + film +
                ", time=" + time +
                ", date=" + date +
                ", lang=" + lang +
                ", freePlacesCount=" + freePlacesCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionInfo that = (SessionInfo) o;
        return id == that.id && freePlacesCount == that.freePlacesCount && film.equals(that.film) && time.equals(that.time) && date.equals(that.date) && lang == that.lang;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
