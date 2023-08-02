package com.example.medicationreminder.patient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.adapter.PatientMedicationListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Appointment;
import com.example.medicationreminder.model.Medication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientMedicationListFragment extends Fragment {

    private static final String TAG = "PatientMedicationListFragment";

    RecyclerView rvMedicationList;
    PatientMedicationListAdapter patientMedicationListAdapter;
    TextView tvEmptyMedicationList;
    ArrayList<Medication> medicationList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference patientDatabaseRef;

    public PatientMedicationListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_medication_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateMedicationList();
    }

    private void initViews(View view) {
        rvMedicationList = view.findViewById(R.id.rvMedicationList);
        medicationList = new ArrayList<>();

        tvEmptyMedicationList = view.findViewById(R.id.tvEmptyMedicationList);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        patientDatabaseRef = firebaseDatabase.getReference(Constants.PATH_PATIENTS);
    }

    private void populateMedicationList() {
        String currentPatientId = "Pat_" + firebaseUser.getUid();

        patientDatabaseRef.child(currentPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Medication medication = childSnapshot.getValue(Medication.class);
                        medicationList.add(medication);
                    }
                }

                Log.d(TAG, "Medication List Count From DB: " + medicationList.size());

                if (medicationList.isEmpty()) {
                    rvMedicationList.setVisibility(View.GONE);
                    tvEmptyMedicationList.setVisibility(View.VISIBLE);
                } else {
                    rvMedicationList.setVisibility(View.VISIBLE);
                    tvEmptyMedicationList.setVisibility(View.GONE);

                    patientMedicationListAdapter = new PatientMedicationListAdapter(requireActivity(), medicationList);
                    rvMedicationList.setAdapter(patientMedicationListAdapter);
                    rvMedicationList.setLayoutManager(new LinearLayoutManager(requireActivity()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}