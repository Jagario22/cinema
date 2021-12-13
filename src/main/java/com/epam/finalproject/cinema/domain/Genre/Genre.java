package com.epam.finalproject.cinema.domain.Genre;

import java.io.Serializable;
import java.util.Objects;
/**
 * Describes genre entity
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class Genre implements Serializable {
    private static final long serialVersionUID = -8546046102831652738L;
    private int id;
    private String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return name.equals(genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
