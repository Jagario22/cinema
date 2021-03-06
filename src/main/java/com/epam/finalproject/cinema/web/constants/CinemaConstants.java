package com.epam.finalproject.cinema.web.constants;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class CinemaConstants {
    public final static int COUNT_OF_ROWS = 5;
    public final static int COUNT_OF_ROW_SEAT = 15;
    public static final int BREAK_MINS = 10;
    public static final LocalTime START_TIME = LocalTime.of(9, 0);
    public static final LocalTime END_TIME = LocalTime.of(0, 0);
    public static final List<String> roles = Arrays.asList("admin", "user");
}
