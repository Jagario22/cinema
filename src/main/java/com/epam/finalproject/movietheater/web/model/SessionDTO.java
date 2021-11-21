package com.epam.finalproject.movietheater.web.model;

import com.epam.finalproject.movietheater.domain.entity.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class SessionDTO {
    private LocalDate date;
    private List<LocalTime> timeList;
    private int id;
    private Session.Lang lang;

    public SessionDTO(LocalDate localDate, int id, Session.Lang lang) {
        this.date = localDate;
        this.id = id;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        SessionDTO that = (SessionDTO) o;
        return id == that.id && Objects.equals(date, that.date) && lang == that.lang;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, id, lang);
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "date=" + date +
                ", timeList=" + timeList +
                ", id=" + id +
                ", lang=" + lang +
                '}';
    }
}
