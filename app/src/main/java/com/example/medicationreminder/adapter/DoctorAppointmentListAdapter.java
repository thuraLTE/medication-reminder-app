package com.example.medicationreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.model.Appointment;
import com.example.medicationreminder.model.Patient;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class DoctorAppointmentListAdapter extends RecyclerView.Adapter<DoctorAppointmentListAdapter.DoctorAppointmentItemViewHolder> {

    Context context;
    List<Appointment> appointmentList;
    List<Patient> patientWithAppointmentList;
    DatabaseReference doctorDatabaseRef;

    public DoctorAppointmentListAdapter(Context context, List<Appointment> appointmentList, List<Patient> patientWithAppointmentList, DatabaseReference doctorDatabaseRef) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.patientWithAppointmentList = patientWithAppointmentList;
        this.doctorDatabaseRef = doctorDatabaseRef;
    }

    public DoctorAppointmentListAdapter() {
    }

    @NonNull
    @Override
    public DoctorAppointmentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_doctor_appointment_info_item, parent, false);
        return new DoctorAppointmentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAppointmentItemViewHolder holder, int position) {
        Patient currentPatient = patientWithAppointmentList.get(position);

        holder.tvPatientName.setText(currentPatient.getPatientFullName());
        holder.tvPatientId.setText(currentPatient.getPatientId());
        holder.tvPatientEmail.setText(currentPatient.getPatientEmail());
        holder.tvPatientPhNum.setText(currentPatient.getPatientPhNum());

        Appointment currentAppointment = appointmentList.get(position);

        holder.tvAppointmentDateAndTime.setText(currentAppointment.getAppointmentDate() + " - " + currentAppointment.getAppointmentTime());
    }

    @Override
    public int getItemCount() {
        return patientWithAppointmentList.size();
    }

    static class DoctorAppointmentItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvAppointmentDateAndTime, tvPatientName, tvPatientId, tvPatientEmail, tvPatientPhNum;

        public DoctorAppointmentItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAppointmentDateAndTime = itemView.findViewById(R.id.tvAppointmentDateAndTime);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientId = itemView.findViewById(R.id.tvPatientId);
            tvPatientEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvPatientPhNum = itemView.findViewById(R.id.tvPatientPhNum);
        }
    }
}
