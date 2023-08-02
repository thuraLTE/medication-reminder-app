package com.example.medicationreminder.model;

public class Day {

    String dayId;
    String dayDescription;

    public Day(String dayId, String dayDescription) {
        this.dayId = dayId;
        this.dayDescription = dayDescription;
    }

    public Day() {
    }

    public String getDayId() {
        return dayId;
    }

    public String getDayDescription() {
        return dayDescription;
    }
}
