package com.epam.finalproject.cinema.web.model.film;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;

import java.util.List;

public class FilmStatistic {
    private FilmInfo film;
    private List<SessionInfo> sessions;


    public FilmStatistic() {
    }

    public FilmStatistic(FilmInfo film, List<SessionInfo> sessions) {
        this.film = film;
        this.sessions = sessions;
    }


    public FilmInfo getFilmInfo() {
        return film;
    }

    public void setFilmInfo(FilmInfo filmInfo) {
        this.film = filmInfo;
    }

    public List<SessionInfo> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionInfo> tickets) {
        this.sessions = tickets;
    }

    @Override
    public String toString() {
        return "FilmStatistic{" +
                "film=" + film +
                ", tickets=" + sessions +
                '}';
    }

}
