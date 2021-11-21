package com.epam.finalproject.movietheater.domain.util;

import java.time.LocalTime;

public class Time {
    private static final int BREAK_TIME = 10;
    private static final int ADV_TIME = 5;
    private static final int START = 9;

    public static void main(String[] args) {
        int[] time = new int[]{1, 1, 1, 2, 1, 2, 1};

        LocalTime localTime = processStart(time[0]);
        for (int i = 1; i < time.length; i++) {
            localTime = process(localTime, time[i]);
        }
    }

    private static LocalTime processStart(int value) {
        LocalTime localTime = LocalTime.of(START, 0);
        System.out.println(value + ": " + localTime);
        localTime = plusTimeToSchedule(value, localTime);

        return localTime;
    }

    private static LocalTime process(LocalTime localTime, int filmId) {
        System.out.println(filmId + ": " + localTime);
        localTime = plusTimeToSchedule(filmId, localTime);
        return localTime;
    }

    private static boolean isRoundMinutes(int minutes) {
        return minutes % 5 == 0;
    }

    private static LocalTime plusTimeToSchedule(int filmId, LocalTime localTime) {
        localTime = addMovieTime(filmId, localTime);
        localTime = localTime.plusMinutes(BREAK_TIME);
        localTime = localTime.plusMinutes(ADV_TIME);

        if (!isRoundMinutes(localTime.getMinute())) {
            localTime = roundMinutesOf(localTime);
        }

        return localTime;
    }

    private static LocalTime roundMinutesOf(LocalTime localTime) {
        int localMinutes = localTime.getMinute();
        int minutes = 0;

        while (!isRoundMinutes(localMinutes)) {
            localMinutes++;
            minutes++;
        }

        localTime = localTime.plusMinutes(minutes);
        return localTime;
    }

    private static LocalTime addMovieTime(int filmId, LocalTime localTime) {
        switch (filmId) {
            case 1:
                localTime = localTime.plusHours(1);
                localTime = localTime.plusMinutes(48);
                break;
            case 2:
                localTime = localTime.plusHours(2);
                localTime = localTime.plusMinutes(38);
                break;
            case 3:
                localTime = localTime.plusHours(2);
                break;
        }

        return localTime;
    }
}
