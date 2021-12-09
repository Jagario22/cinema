package com.epam.finalproject.cinema.web.model.film.session;

import com.epam.finalproject.cinema.domain.session.Session;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SessionsInfoGroupByDate implements Serializable {
    private LocalDate date;
    private List<SessionInfo> sessionsInfo;
    private Session.Lang lang;

    public SessionsInfoGroupByDate(LocalDate localDate, Session.Lang lang) {
        this.date = localDate;
        this.lang = lang;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<SessionInfo> getSessionsInfo() {
        if (sessionsInfo == null) {
            sessionsInfo = new ArrayList<>();
        }
        return sessionsInfo;
    }

    public void setSessionsInfo(List<SessionInfo> sessionsSessionPlacesInfo) {
        this.sessionsInfo = sessionsSessionPlacesInfo;
    }

    public Session.Lang getLang() {
        return lang;
    }

    public void setLang(Session.Lang lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "SessionsInfoGroupByDate{" +
                "date=" + date +
                ", sessions=" + sessionsInfo +
                ", lang=" + lang +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionsInfoGroupByDate that = (SessionsInfoGroupByDate) o;
        return Objects.equals(date, that.date) && Objects.equals(sessionsInfo, that.sessionsInfo) && lang == that.lang;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, sessionsInfo, lang);
    }
}
