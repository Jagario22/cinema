package com.epam.finalproject.cinema.util;

public class FilmInfoValidationUtil {

    public boolean isValidTitle(String title) {
        return title.matches("^[A-Z]([a-z\\s]){3,100}$");
    }

    public boolean isValidLen(int len) {
        return len > 0 && len < 240;
    }

    public boolean isValidYear(String year) {
        return year.matches("^([0-9]){4}$");
    }

    public boolean isValidDescr(String descr) {
        return descr.matches("^[A-Z]([A-Za-z\\s\\-.?!0-9)(,:\"']){1,3000}$");
    }


}
