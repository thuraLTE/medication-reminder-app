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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Doctor;
import com.example.medicationreminder.model.Patient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminDoctorListAdapter extends RecyclerView.Adapter<AdminDoctorListAdapter.AdminDoctorItemViewHolder> {

    Context context;
    List<Doctor> doctorList;
    FirebaseUser firebaseUser;
    DatabaseReference patientDatabaseRef;
    DatabaseReference doctorDatabaseRef;
    Patient currentPatient;
    Boolean isPatient;

    public AdminDoctorListAdapter(Context context, List<Doctor> doctorList, Boolean isPatient, @Nullable FirebaseUser firebaseUser, @Nullable DatabaseReference patientDatabaseRef, @Nullable DatabaseReference doctorDatabaseRef) {
        this.context = context;
        this.doctorList = doctorList;
        this.firebaseUser = firebaseUser;
        this.patientDatabaseRef = patientDatabaseRef;
        this.doctorDatabaseRef = doctorDatabaseRef;
        this.isPatient = isPatient;
    }

    public void refreshDoctorList(List<Doctor> newFilteredDoctorList) {
        doctorList.clear();
        doctorList.addAll(newFilteredDoctorList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminDoctorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_admin_doctor_info_item, parent, false);
        return new AdminDoctorItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDoctorItemViewHolder holder, int position) {
        Doctor currentDoctor = doctorList.get(position);

        holder.tvDoctorName.setText(currentDoctor.getDoctorFullName());
        holder.tvDoctorId.setText(currentDoctor.getDoctorId());
        holder.tvDoctorSpecialization.setText(currentDoctor.getDoctorSpecialization());
        holder.tvDoctorQualification.setText(currentDoctor.getDoctorQualification());
        holder.tvDoctorEmail.setText(currentDoctor.getDoctorEmail());
        holder.tvDoctorPhNum.setText(currentDoctor.getDoctorPhNum());

        if (isPatient) {
            String currentUserId = "Pat_" + firebaseUser.getUid();
            patientDatabaseRef.child(currentUserId).child("connDoctor").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Doctor connDoctor = snapshot.getValue(Doctor.class);

                    if (connDoctor == null || !currentDoctor.getDoctorId().equals(connDoctor.getDoctorId())) {

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PopupMenu popupMenu = new PopupMenu(context, view);
                                popupMenu.getMenuInflater().inflate(R.menu.doctor_list_subscribe_menu, popupMenu.getMenu());
                                popupMenu.show();

                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        if (menuItem.getItemId() == R.id.menuSubscribeDoctor) {

                                            // Update ConnDoc
                                            patientDatabaseRef.child(currentUserId).child("connDoctor").setValue(currentDoctor);

                                            // Update ConnPat
                                            patientDatabaseRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    currentPatient = snapshot.getValue(Patient.class);

                                                    doctorDatabaseRef.child(currentDoctor.getDoctorId()).child(Constants.PATH_CONN_PATIENTS).child(currentUserId).setValue(currentPatient);

                                                    Toast.makeText(context, "Doctor Subscribed", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            return true;
                                        } else return false;
                                    }
                                });
                            }
                        });
                    } else {
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PopupMenu popupMenu = new PopupMenu(context, view);
                                popupMenu.getMenuInflater().inflate(R.menu.doctor_list_unsubscribe_menu, popupMenu.getMenu());
                                popupMenu.show();

                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        if (menuItem.getItemId() == R.id.menuUnsubscribeDoctor) {

                                            // Update ConnDoc
                                            patientDatabaseRef.child(currentUserId).child("connDoctor").setValue(null);

                                            // Update ConnPat
                                            doctorDatabaseRef.child(currentDoctor.getDoctorId()).child(Constants.PATH_CONN_PATIENTS).child(currentUserId).setValue(null);

                                            Toast.makeText(context, "Doctor Subscription Cancelled", Toast.LENGTH_SHORT).show();

                                            return true;
                                        } else return false;
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class AdminDoctorItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvDoctorName, tvDoctorId, tvDoctorSpecialization, tvDoctorQualification, tvDoctorEmail, tvDoctorPhNum;

        public AdminDoctorItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorId = itemView.findViewById(R.id.tvDoctorId);
            tvDoctorSpecialization = itemView.findViewById(R.id.tvDoctorSpecialization);
            tvDoctorQualification = itemView.findViewById(R.id.tvDoctorQualification);
            tvDoctorEmail = itemView.findViewById(R.id.tvDoctorEmail);
            tvDoctorPhNum = itemView.findViewById(R.id.tvDoctorPhNum);
        }
    }
}
