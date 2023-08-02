package com.example.medicationreminder.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Patient implements Serializable {

    String patientId;
    String patientFullName;
    String patientEmail;
    String patientPhNum;
    String patientCase;
    Doctor connDoctor;
    HashMap<String, Medication> patientMedications;
    HashMap<String, Appointment> patientAppointments;

    public Patient(String patientId, String patientFullName, String patientEmail, String patientPhNum, @Nullable String patientCase, @Nullable Doctor connDoctor, @Nullable HashMap<String, Medication> medicationList, @Nullable HashMap<String, Appointment> appointmentList) {
        this.patientId = patientId;
        this.patientFullName = patientFullName;
        this.patientEmail = patientEmail;
        this.patientPhNum = patientPhNum;
        this.patientCase = patientCase;
        this.connDoctor = connDoctor;
        this.patientMedications = medicationList;
        this.patientAppointments = appointmentList;
    }

    public Patient() {
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientFullName() {
        return patientFullName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhNum() {
        return patientPhNum;
    }

    public String getPatientCase() {
        return patientCase;
    }

    public Doctor getConnDoctor() {
        return connDoctor;
    }

    public HashMap<String, Medication> getPatientMedications() {
        return patientMedications;
    }

    public HashMap<String, Appointment> getPatientAppointments() {
        return patientAppointments;
    }

}


