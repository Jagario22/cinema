package com.epam.finalproject.movietheater.domain.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.Objects;

public class Film implements Serializable, Comparable<Film> {
    private static final long serialVersionUID = -5933752047457614880L;
    private int id;
    private String title;
    private int len;
    private String year;
    private int category;
    private String descr;
    private float rating;
    private String img;
    private Date lastShowingDate;

    public Film(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Film(String title, String year, String descr, String img) {
        this.title = title;
        this.year = year;
        this.descr = descr;
        this.img = img;
    }

    public Film(String title, Integer len, String year, Integer category, String descr, float rating, String img) {
        this.title = title;
        this.len = len;
        this.year = year;
        this.category = category;
        this.descr = descr;
        this.rating = rating;
        this.img = img;
    }


    public Film(int id, String title, int len, String year, int category, String descr,
                float rating, String img, Date lastShowingDate) {
        this.id = id;
        this.title = title;
        this.len = len;
        this.year = year;
        this.category = category;
        this.descr = descr;
        this.rating = rating;
        this.img = img;
        this.lastShowingDate = lastShowingDate;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getLastShowingDate() {
        return lastShowingDate;
    }

    public void setLastShowingDate(Date lastShowingDate) {
        this.lastShowingDate = lastShowingDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return len == film.len && category == film.category && Float.compare(film.rating, rating) == 0 && title.equals(film.title) && year.equals(film.year) && descr.equals(film.descr) && img.equals(film.img) && lastShowingDate.equals(film.lastShowingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year, descr);
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", len=" + len +
                ", year='" + year + '\'' +
                ", category=" + category +
                ", descr='" + descr + '\'' +
                ", rating=" + rating +
                ", img='" + img + '\'' +
                ", lastShowingDate=" + lastShowingDate +
                '}';
    }

    @Override
    public int compareTo(Film film) {
        return this.getTitle().compareTo(film.getTitle());
    }
}
