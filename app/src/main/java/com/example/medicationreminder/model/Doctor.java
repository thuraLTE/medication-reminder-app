package com.example.medicationreminder.model;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class Doctor {

    String doctorId;
    String doctorFullName;
    String doctorEmail;
    String doctorPhNum;
    String doctorSpecialization;
    String doctorQualification;
    HashMap<String, Patient> connPatients;

    public Doctor(String doctorId, String doctorFullName, String doctorEmail, String doctorPhNum, String doctorSpecialization, String doctorQualification, @Nullable HashMap<String, Patient> connPatients) {
        this.doctorId = doctorId;
        this.doctorFullName = doctorFullName;
        this.doctorEmail = doctorEmail;
        this.doctorPhNum = doctorPhNum;
        this.doctorSpecialization = doctorSpecialization;
        this.doctorQualification = doctorQualification;
        this.connPatients = connPatients;
    }

    public Doctor(String doctorId, String doctorFullName) {
        this.doctorId = doctorId;
        this.doctorFullName = doctorFullName;
    }

    public Doctor() {
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public String getDoctorPhNum() {
        return doctorPhNum;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public String getDoctorQualification() {
        return doctorQualification;
    }
}
