package com.example.medicationreminder.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Medication {

    String medicationId;
    String medicationName;
    String type;
    int dose;
    String unit;
    boolean isDailyBased;
    HashMap<String, Day> selectedDaysOfWeek;
    HashMap<String, ReminderTime> selectedReminderTimes;
    String startDate;
    boolean hasEndDate;
    String endDate;
    String intakeInstructions;

    public Medication(String medicationId, String medicationName, String type, int dose, String unit, boolean isDailyBased, @Nullable HashMap<String, Day> selectedDaysOfWeek, @Nullable HashMap<String, ReminderTime> selectedReminderTimes, String startDate, boolean hasEndDate, @Nullable String endDate, @Nullable String intakeInstructions) {
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.type = type;
        this.dose = dose;
        this.unit = unit;
        this.isDailyBased = isDailyBased;
        this.selectedDaysOfWeek = selectedDaysOfWeek;
        this.selectedReminderTimes = selectedReminderTimes;
        this.startDate = startDate;
        this.hasEndDate = hasEndDate;
        this.endDate = endDate;
        this.intakeInstructions = intakeInstructions;
    }

    public Medication() {
    }

    public String getMedicationId() {
        return medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getType() {
        return type;
    }

    public int getDose() {
        return dose;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isDailyBased() {
        return isDailyBased;
    }

    public HashMap<String, Day> getSelectedDaysOfWeek() {
        return selectedDaysOfWeek;
    }

    public HashMap<String, ReminderTime> getSelectedReminderTimes() {
        return selectedReminderTimes;
    }

    public String getStartDate() {
        return startDate;
    }

    public boolean getHasEndDate() {
        return hasEndDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getIntakeInstructions() {
        return intakeInstructions;
    }
}
