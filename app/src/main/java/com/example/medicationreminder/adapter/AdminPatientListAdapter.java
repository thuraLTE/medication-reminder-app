package com.example.medicationreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Doctor;
import com.example.medicationreminder.model.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminPatientListAdapter extends RecyclerView.Adapter<AdminPatientListAdapter.AdminPatientItemViewHolder> {

    Context context;
    List<Patient> patientList;
    DatabaseReference patientDatabaseRef;
    DatabaseReference doctorDatabaseRef;

    public AdminPatientListAdapter(Context context, List<Patient> patientList, DatabaseReference patientDatabaseRef, DatabaseReference doctorDatabaseRef) {
        this.context = context;
        this.patientList = patientList;
        this.patientDatabaseRef = patientDatabaseRef;
        this.doctorDatabaseRef = doctorDatabaseRef;
    }

    public void refreshPatientList(List<Patient> newFilteredPatientList) {
        patientList.clear();
        patientList.addAll(newFilteredPatientList);
        notifyDataSetChanged();
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.admin_delete_patient_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String currentPatientId = patientList.get(holder.getBindingAdapterPosition()).getPatientId();

                        if (menuItem.getItemId() == R.id.menuDeletePatient) {

                            patientDatabaseRef.child(currentPatientId).removeValue();

                            doctorDatabaseRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot childSnapSnot : snapshot.getChildren()) {
                                            if (childSnapSnot.exists()) {
                                                for (DataSnapshot childSnapShotTwo : childSnapSnot.getChildren()) {

                                                    String connPatientId = childSnapShotTwo.getValue(String.class);

                                                    assert connPatientId != null;
                                                    if (connPatientId.equals(currentPatientId)) {
                                                        childSnapShotTwo.getRef().removeValue();

                                                        Toast.makeText(context, "Patient Removed Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            patientList.remove(holder.getBindingAdapterPosition());
                            refreshPatientList(patientList);

                            return true;
                        } else return false;
                    }
                });
            }
        });
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
