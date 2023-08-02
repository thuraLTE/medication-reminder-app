package com.example.medicationreminder.doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.adapter.DoctorAppointmentListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Appointment;
import com.example.medicationreminder.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorAppointmentListFragment extends Fragment {

    RecyclerView rvAppointmentList;
    DoctorAppointmentListAdapter doctorAppointmentListAdapter;
    TextView tvEmptyAppointmentList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference doctorDatabaseRef;
    ArrayList<Appointment> appointmentList;
    ArrayList<Patient> patientWithAppointmentList;

    public DoctorAppointmentListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_appointment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateAppointmentList();
    }

    private void initViews(View view) {
        rvAppointmentList = view.findViewById(R.id.rvAppointmentList);

        tvEmptyAppointmentList = view.findViewById(R.id.tvEmptyAppointmentList);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);

        appointmentList = new ArrayList<>();
        patientWithAppointmentList = new ArrayList<>();
    }

    private void populateAppointmentList() {
        String currentDoctorId = "Doc_" + firebaseUser.getUid();

        doctorDatabaseRef.child(currentDoctorId).child(Constants.PATH_CONN_PATIENTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvEmptyAppointmentList.setVisibility(View.GONE);
                    rvAppointmentList.setVisibility(View.VISIBLE);

                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        if (childSnapshot.child(Constants.PATH_PATIENT_APPOINTMENTS).exists()) {
                            for (DataSnapshot appointmentSnapshot : childSnapshot.child(Constants.PATH_PATIENT_APPOINTMENTS).getChildren()) {
                                Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                                appointmentList.add(appointment);
                            }

                            Patient patient = childSnapshot.getValue(Patient.class);
                            patientWithAppointmentList.add(patient);
                        }
                    }

                    if (!appointmentList.isEmpty() && !patientWithAppointmentList.isEmpty()) {
                        if (getActivity() != null && isAdded()) {
                            doctorAppointmentListAdapter = new DoctorAppointmentListAdapter(getActivity(), appointmentList, patientWithAppointmentList, doctorDatabaseRef);
                            rvAppointmentList.setAdapter(doctorAppointmentListAdapter);
                            rvAppointmentList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }
                    } else {
                        tvEmptyAppointmentList.setVisibility(View.VISIBLE);
                        rvAppointmentList.setVisibility(View.GONE);
                    }
                } else {
                    tvEmptyAppointmentList.setVisibility(View.VISIBLE);
                    rvAppointmentList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}