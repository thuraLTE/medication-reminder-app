package com.example.medicationreminder.model;

public class ReminderTime {

    String timeId;
    String timeDescription;

    public ReminderTime(String timeId, String timeDescription) {
        this.timeId = timeId;
        this.timeDescription = timeDescription;
    }

    public ReminderTime() {
    }

    public String getTimeId() {
        return timeId;
    }

    public String getTimeDescription() {
        return timeDescription;
    }
}
