package com.example.medicationreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class DoctorPatientListAdapter extends RecyclerView.Adapter<DoctorPatientListAdapter.DoctorPatientItemViewHolder> {

    Context context;
    ArrayList<Patient> patientList;
    DoctorOnPatientItemClicked listener;

    public void refreshPatientList(List<Patient> newFilteredPatientList) {
        patientList.clear();
        patientList.addAll(newFilteredPatientList);
        notifyDataSetChanged();
    }

    public DoctorPatientListAdapter(Context context, ArrayList<Patient> patientList, DoctorOnPatientItemClicked listener) {
        this.context = context;
        this.patientList = patientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorPatientItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_doctor_patient_info_item, parent, false);
        return new DoctorPatientItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorPatientItemViewHolder holder, int position) {
        Patient currentPatient = patientList.get(position);

        holder.tvPatientName.setText(currentPatient.getPatientFullName());
        holder.tvPatientId.setText(currentPatient.getPatientId());
        holder.tvPatientEmail.setText(currentPatient.getPatientEmail());
        holder.tvPatientPhNum.setText(currentPatient.getPatientPhNum());

        if (currentPatient.getPatientMedications() == null) {
            holder.tvPatientMedicationAmount.setVisibility(View.GONE);
        } else {
            holder.tvPatientMedicationAmount.setText(context.getString(R.string.medication_amount_placeholder, currentPatient.getPatientMedications().size()));
        }

        if (currentPatient.getPatientAppointments() == null) {
            holder.tvPatientAppointments.setVisibility(View.GONE);
        } else {
            holder.tvPatientAppointments.setText(context.getString(R.string.appointment_amount_placeholder, currentPatient.getPatientAppointments().size()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listener.onPatientClicked(view, currentPatient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class DoctorPatientItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvPatientName, tvPatientId, tvPatientMedicationAmount, tvPatientAppointments, tvPatientEmail, tvPatientPhNum;

        public DoctorPatientItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientId = itemView.findViewById(R.id.tvPatientId);
            tvPatientMedicationAmount = itemView.findViewById(R.id.tvPatientMedicationAmount);
            tvPatientAppointments = itemView.findViewById(R.id.tvPatientAppointments);
            tvPatientEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvPatientPhNum = itemView.findViewById(R.id.tvPatientPhNum);
        }
    }

    public interface DoctorOnPatientItemClicked {
        public default void onPatientClicked(View view, Patient patient) {}
    }
}
