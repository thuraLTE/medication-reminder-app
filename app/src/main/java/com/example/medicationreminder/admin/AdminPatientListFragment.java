package com.example.medicationreminder.admin;

import android.content.Context;
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
import com.example.medicationreminder.adapter.AdminPatientListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminPatientListFragment extends Fragment {

    private static final String TAG = "AdminPatientListFragment";

    Context context;
    TextView tvEmptyPatientList;
    RecyclerView rvAdminPatientList;
    AdminPatientListAdapter adminPatientListAdapter;
    ArrayList<Patient> patientList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference patientDatabaseRef;
    DatabaseReference doctorDatabaseRef;

    public AdminPatientListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_patient_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populatePatientList();
    }

    private void initViews(View view) {
        tvEmptyPatientList = view.findViewById(R.id.tvEmptyPatientList);

        rvAdminPatientList = view.findViewById(R.id.rvAdminPatientList);
        patientList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);
    }

    private void populatePatientList() {
        patientList.clear();

        // Retrieve list of data from FB
        patientDatabaseRef = firebaseDatabase.getReference(Constants.PATH_PATIENTS);
        patientDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Patient patient = childSnapshot.getValue(Patient.class);
                    patientList.add(patient);
                }

                Log.d(TAG, "Patients Count: " + patientList.size());

                if (patientList.isEmpty()) {
                    tvEmptyPatientList.setVisibility(View.VISIBLE);
                    rvAdminPatientList.setVisibility(View.GONE);

                } else {
                    tvEmptyPatientList.setVisibility(View.GONE);
                    rvAdminPatientList.setVisibility(View.VISIBLE);

                    adminPatientListAdapter = new AdminPatientListAdapter(context, patientList, patientDatabaseRef, doctorDatabaseRef);
                    rvAdminPatientList.setAdapter(adminPatientListAdapter);
                    rvAdminPatientList.setLayoutManager(new LinearLayoutManager(context));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}