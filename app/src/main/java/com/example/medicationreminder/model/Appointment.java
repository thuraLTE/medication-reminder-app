package com.example.medicationreminder.model;

import androidx.annotation.Nullable;

public class Appointment {

    String appointmentId;
    String appointmentDate;
    String appointmentTime;

    public Appointment(@Nullable String appointmentId, String appointmentDate, String appointmentTime) {
        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    public Appointment() {
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }
}
