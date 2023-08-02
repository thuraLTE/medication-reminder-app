package com.example.medicationreminder.admin;

import android.content.Context;
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
import com.example.medicationreminder.adapter.AdminDoctorListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;

public class AdminDoctorListFragment extends Fragment {

    private static final String TAG = "AdminDoctorListFragment";

    Context context;
    TextView tvEmptyDoctorList;
    FloatingActionButton fabAddNewDoctor;
    SwipeableRecyclerView rvDoctorList;
    AdminDoctorListAdapter adminDoctorListAdapter;
    ArrayList<Doctor> doctorList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference doctorDatabaseRef;
    Boolean isPatient = false;

    public AdminDoctorListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_doctor_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateDoctorList();
        initEvents();
    }

    private void initViews(View view) {
        tvEmptyDoctorList = view.findViewById(R.id.tvEmptyDoctorList);

        fabAddNewDoctor = view.findViewById(R.id.fabAddNewDoctor);

        rvDoctorList = view.findViewById(R.id.rvDoctorList);
        doctorList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void populateDoctorList() {
        doctorList.clear();

        // Retrieve list of data from FB
        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);
        doctorDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Doctor doctor = childSnapshot.getValue(Doctor.class);
                    doctorList.add(doctor);
                }

                Log.d(TAG, "Doctors Count: " + doctorList.size());

                if (doctorList.isEmpty()) {
                    tvEmptyDoctorList.setVisibility(View.VISIBLE);
                    rvDoctorList.setVisibility(View.GONE);

                } else {
                    tvEmptyDoctorList.setVisibility(View.GONE);
                    rvDoctorList.setVisibility(View.VISIBLE);

                    adminDoctorListAdapter = new AdminDoctorListAdapter(context, doctorList, isPatient, null, null, doctorDatabaseRef);
                    rvDoctorList.setAdapter(adminDoctorListAdapter);
                    rvDoctorList.setLayoutManager(new LinearLayoutManager(context));

                    rvDoctorList.setListener(new SwipeLeftRightCallback.Listener() {
                        @Override
                        public void onSwipedLeft(int position) {

                        }

                        @Override
                        public void onSwipedRight(int position) {
                            doctorDatabaseRef.child(doctorList.get(position).getDoctorId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.d(TAG, "Doctor deleted successfully");
                                    else
                                        Log.d(TAG, "Failure to delete doctor");
                                }
                            });

                            doctorList.remove(position);
                            adminDoctorListAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initEvents() {
        fabAddNewDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.adminFragmentContainer, AdminAddNewDoctorFragment.class, null).commit();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}