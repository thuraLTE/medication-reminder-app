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

import java.util.List;

public class AdminPatientListAdapter extends RecyclerView.Adapter<AdminPatientListAdapter.AdminPatientItemViewHolder> {

    Context context;
    List<Patient> patientList;

    public AdminPatientListAdapter(Context context, List<Patient> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public AdminPatientItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_admin_patient_info_item, parent, false);
        return new AdminPatientItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPatientItemViewHolder holder, int position) {
        Patient currentPatient = patientList.get(position);

        holder.tvPatientName.setText(currentPatient.getPatientFullName());
        holder.tvPatientId.setText(currentPatient.getPatientId());
        holder.tvPatientEmail.setText(currentPatient.getPatientEmail());
        holder.tvPatientPhNum.setText(currentPatient.getPatientPhNum());

        if (currentPatient.getPatientCase() != null) {
            holder.tvPatientCase.setVisibility(View.VISIBLE);
            holder.tvPatientCase.setText(currentPatient.getPatientCase());
        } else {
            holder.tvPatientCase.setVisibility(View.GONE);
        }

        if (currentPatient.getConnDoctor() != null) {
            holder.tvDoctorName.setVisibility(View.VISIBLE);
            holder.tvDoctorId.setVisibility(View.VISIBLE);

            holder.tvDoctorName.setText(currentPatient.getConnDoctor().getDoctorFullName());
            holder.tvDoctorId.setText(currentPatient.getConnDoctor().getDoctorId());
        } else {
            holder.tvDoctorName.setVisibility(View.GONE);
            holder.tvDoctorId.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class AdminPatientItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvPatientName, tvPatientId, tvPatientEmail, tvPatientPhNum, tvPatientCase, tvDoctorName, tvDoctorId;

        public AdminPatientItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientId = itemView.findViewById(R.id.tvPatientId);
            tvPatientEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvPatientPhNum = itemView.findViewById(R.id.tvPatientPhNum);
            tvPatientCase = itemView.findViewById(R.id.tvPatientCase);

            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorId = itemView.findViewById(R.id.tvDoctorId);
        }
    }
}
