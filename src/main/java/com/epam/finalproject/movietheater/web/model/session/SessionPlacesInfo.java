package com.epam.finalproject.movietheater.web.model.session;

import java.time.LocalTime;

public class SessionPlacesInfo {
    private int sessionId;
    private int freePlacesCount;
    private LocalTime sessionTime;

    public SessionPlacesInfo(int sessionId, int freePlacesCount, LocalTime sessionTime) {
        this.sessionId = sessionId;
        this.freePlacesCount = freePlacesCount;
        this.sessionTime = sessionTime;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getFreePlacesCount() {
        return freePlacesCount;
    }

    public void setFreePlacesCount(int freePlacesCount) {
        this.freePlacesCount = freePlacesCount;
    }

    public LocalTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalTime sessionTime) {
        this.sessionTime = sessionTime;
    }
}

