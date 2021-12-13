package com.epam.finalproject.cinema.util;

import java.time.LocalDate;
/**
 * Provides methods for validation fields of Film object
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
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

    public boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 10;
    }

    public boolean isValidLastShowingDate(LocalDate lastShowingDate) {
        return lastShowingDate.isAfter(LocalDate.now());
    }
}
