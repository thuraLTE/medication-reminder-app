package com.example.medicationreminder.patient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.adapter.AdminDoctorListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Doctor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientDoctorListFragment extends Fragment {

    private static final String TAG = "PatientDoctorListFragment";

    EditText edtSearchDoctors;
    Spinner spinnerDoctorCategory;
    RecyclerView rvDoctorList;
    TextView tvEmptyDoctorList;
    AdminDoctorListAdapter adminDoctorListAdapter;
    ArrayList<Doctor> doctorList;
    ProgressBar progressBar;
    FloatingActionButton fabMyDoctorList;
    Handler handler;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference doctorDatabaseRef;
    DatabaseReference patientDatabaseRef;
    Boolean isPatient = true;

    public PatientDoctorListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_doctor_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateAllDoctorList();
        setUpDoctorCategorySpinner();
        initEvents();
    }

    private void initViews(View view) {
        edtSearchDoctors = view.findViewById(R.id.edtSearchDoctors);
        rvDoctorList = view.findViewById(R.id.rvDoctorList);

        tvEmptyDoctorList = view.findViewById(R.id.tvEmptyDoctorList);

        progressBar = view.findViewById(R.id.progressBar);

        spinnerDoctorCategory = view.findViewById(R.id.spinnerDoctorCategory);

        fabMyDoctorList = view.findViewById(R.id.fabMyDoctor);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);
        patientDatabaseRef = firebaseDatabase.getReference(Constants.PATH_PATIENTS);
        doctorList = new ArrayList<>();

        handler = new Handler();
    }

    private void populateAllDoctorList() {

        reloadDoctorList();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                doctorDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorList.clear();

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Doctor doctor = childSnapshot.getValue(Doctor.class);
                            doctorList.add(doctor);
                        }

                        Log.d(TAG, "Doctors Count: " + doctorList.size());

                        if (doctorList.isEmpty()) {
                            rvDoctorList.setVisibility(View.GONE);
                            tvEmptyDoctorList.setVisibility(View.VISIBLE);
                        } else {
                            rvDoctorList.setVisibility(View.VISIBLE);
                            tvEmptyDoctorList.setVisibility(View.GONE);

                            if (getActivity() != null && isAdded()) {
                                adminDoctorListAdapter = new AdminDoctorListAdapter(getActivity(), doctorList, isPatient, firebaseUser, patientDatabaseRef, doctorDatabaseRef);

                                rvDoctorList.setAdapter(adminDoctorListAdapter);
                                rvDoctorList.setLayoutManager(new LinearLayoutManager(getActivity()));

                                attachTextWatcher(doctorList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, Constants.PROGRESS_BAR_TIME);
    }

    private void setUpDoctorCategorySpinner() {
        ArrayAdapter<CharSequence> spinnerDoctorCategoryAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.doctor_category_array, R.layout.spinner_item_one);
        spinnerDoctorCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_one);

        spinnerDoctorCategory.setAdapter(spinnerDoctorCategoryAdapter);

        spinnerDoctorCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    filterDoctorListByCategory("Endocrinologist");
                } else if (i == 2) {
                    filterDoctorListByCategory("Gastroenterologist");
                } else if (i == 3) {
                    filterDoctorListByCategory("Cardiologist");
                } else if (i == 4) {
                    filterDoctorListByCategory("Dermatologist");
                } else if (i == 5) {
                    filterDoctorListByCategory("Neurologist");
                } else if (i == 6) {
                    filterDoctorListByCategory("Urologist");
                } else if (i == 7) {
                    filterDoctorListByCategory("Orthopaedist");
                } else if (i == 8) {
                    filterDoctorListByCategory("Ophthalmologist");
                } else if (i == 9) {
                    filterDoctorListByCategory("Oncologist");
                } else if (i == 10) {
                    filterDoctorListByCategory("Psychiatrist");
                } else {
                    populateAllDoctorList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void reloadDoctorList() {
        progressBar.setVisibility(View.VISIBLE);
        rvDoctorList.setVisibility(View.GONE);
        tvEmptyDoctorList.setVisibility(View.GONE);
    }

    private void filterDoctorListByCategory(String category) {
        reloadDoctorList();

        doctorDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Doctor doctor = childSnapshot.getValue(Doctor.class);
                    doctorList.add(doctor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                Log.d(TAG, "All Doctors Count: " + doctorList.size());

                ArrayList<Doctor> categorizedDoctorList = new ArrayList<>();

                for (Doctor doctor : doctorList) {
                    if (doctor.getDoctorSpecialization().equalsIgnoreCase(category)) {
                        categorizedDoctorList.add(doctor);
                    }
                }

                if (categorizedDoctorList.isEmpty()) {
                    rvDoctorList.setVisibility(View.GONE);
                    tvEmptyDoctorList.setVisibility(View.VISIBLE);
                } else {
                    rvDoctorList.setVisibility(View.VISIBLE);
                    tvEmptyDoctorList.setVisibility(View.GONE);

                    adminDoctorListAdapter.refreshDoctorList(categorizedDoctorList);

                    attachTextWatcher(categorizedDoctorList);
                }
            }
        }, Constants.PROGRESS_BAR_TIME);
    }

    private void attachTextWatcher(ArrayList<Doctor> givenDoctorList) {
        edtSearchDoctors.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userQuery = charSequence.toString();
                ArrayList<Doctor> userQueryDoctorList = new ArrayList<>();

                if (!givenDoctorList.isEmpty()) {
                    Log.d(TAG, "Given Doctors Count: " + givenDoctorList.size());
                    if (!TextUtils.isEmpty(userQuery)) {
                        for (Doctor doctor : givenDoctorList) {
                            if (doctor.getDoctorFullName().contains(userQuery)) {
                                userQueryDoctorList.add(doctor);
                            }
                        }

                        if (userQueryDoctorList.isEmpty()) {
                            rvDoctorList.setVisibility(View.GONE);
                            tvEmptyDoctorList.setVisibility(View.VISIBLE);
                        } else {
                            rvDoctorList.setVisibility(View.VISIBLE);
                            tvEmptyDoctorList.setVisibility(View.GONE);
                            adminDoctorListAdapter.refreshDoctorList(userQueryDoctorList);
                        }
                    } else {
                        if (spinnerDoctorCategory.getSelectedItemPosition() != 0)
                            filterDoctorListByCategory(spinnerDoctorCategory.getSelectedItem().toString());
                        else
                            populateAllDoctorList();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initEvents() {
        fabMyDoctorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.patientFragmentContainer, PatientMyDoctorFragment.class, null).commit();
//                requireActivity().getActionBar().setTitle("My Doctor");
            }
        });
    }
}