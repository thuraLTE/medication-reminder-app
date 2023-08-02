package com.example.medicationreminder.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.adapter.AdminPatientListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;

public class AdminPatientListFragment extends Fragment {

    private static final String TAG = "AdminPatientListFragment";

    TextView tvEmptyPatientList;
    SwipeableRecyclerView rvPatientList;
    AdminPatientListAdapter adminPatientListAdapter;
    ArrayList<Patient> patientList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference patientDatabaseRef;

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

        rvPatientList = view.findViewById(R.id.rvPatientList);
        patientList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
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
                    rvPatientList.setVisibility(View.GONE);

                } else {
                    tvEmptyPatientList.setVisibility(View.GONE);
                    rvPatientList.setVisibility(View.VISIBLE);

                    adminPatientListAdapter = new AdminPatientListAdapter(requireContext(), patientList);
                    rvPatientList.setAdapter(adminPatientListAdapter);
                    rvPatientList.setLayoutManager(new LinearLayoutManager(requireContext()));

                    rvPatientList.setListener(new SwipeLeftRightCallback.Listener() {
                        @Override
                        public void onSwipedLeft(int position) {

                        }

                        @Override
                        public void onSwipedRight(int position) {
                            patientDatabaseRef.child(patientList.get(position).getPatientId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.d(TAG, "Patient deleted successfully");
                                    else
                                        Log.d(TAG, "Failure to delete patient");
                                }
                            });

                            patientList.remove(position);
                            adminPatientListAdapter.notifyDataSetChanged();
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