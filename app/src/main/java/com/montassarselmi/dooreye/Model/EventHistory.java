package com.montassarselmi.dooreye.Model;

public class EventHistory {

    private int id;
    private String eventTime;

    public EventHistory(int id, String eventTime) {
        this.id = id;
        this.eventTime = eventTime;
    }

    public EventHistory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
