package com.example.medicationreminder.doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.adapter.DoctorPatientListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorMyPatientListFragment extends Fragment implements DoctorPatientListAdapter.DoctorOnPatientItemClicked {

    private static final String TAG = "DoctorMyPatientListFragment";

    RecyclerView rvPatientList;
    DoctorPatientListAdapter doctorPatientListAdapter;
    TextView tvEmptyMyPatientList;
    EditText edtSearchMyPatients;
    ProgressBar progressBar;
    Handler handler;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference doctorDatabaseRef;
    ArrayList<Patient> myPatientsList;

    public DoctorMyPatientListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_my_patient_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateMyPatientsList();
    }

    private void initViews(View view) {
        rvPatientList = view.findViewById(R.id.rvPatientList);

        tvEmptyMyPatientList = view.findViewById(R.id.tvEmptyMyPatientList);

        edtSearchMyPatients = view.findViewById(R.id.edtSearchMyPatients);

        progressBar = view.findViewById(R.id.progressBar);
        handler = new Handler(Looper.getMainLooper());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);

        myPatientsList = new ArrayList<>();
    }

    private void populateMyPatientsList() {
        reloadMyPatientList();

        String currentDoctorId = "Doc_" + firebaseUser.getUid();

        handler.postDelayed(() -> {
            doctorDatabaseRef.child(currentDoctorId).child(Constants.PATH_CONN_PATIENTS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Patient patient = childSnapshot.getValue(Patient.class);
                        myPatientsList.add(patient);
                    }
                    Log.d(TAG, "My Patients Count: " + myPatientsList.size());

                    if (!myPatientsList.isEmpty()) {
                        if (getActivity() != null && isAdded()) {

                            progressBar.setVisibility(View.GONE);
                            tvEmptyMyPatientList.setVisibility(View.GONE);
                            rvPatientList.setVisibility(View.VISIBLE);

                            doctorPatientListAdapter = new DoctorPatientListAdapter(getActivity(), myPatientsList, DoctorMyPatientListFragment.this);
                            rvPatientList.setAdapter(doctorPatientListAdapter);
                            rvPatientList.setLayoutManager(new LinearLayoutManager(getActivity()));

                            attachTextWatcher();
                        }

                    } else {
                        progressBar.setVisibility(View.GONE);
                        rvPatientList.setVisibility(View.GONE);
                        tvEmptyMyPatientList.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }, Constants.PROGRESS_BAR_TIME);
    }

    private void reloadMyPatientList() {
        progressBar.setVisibility(View.VISIBLE);
        rvPatientList.setVisibility(View.GONE);
    }

    private void attachTextWatcher() {
        edtSearchMyPatients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userQuery = charSequence.toString();
                ArrayList<Patient> userQueryPatientList = new ArrayList<>();

                if (!TextUtils.isEmpty(userQuery)) {
                    for (Patient patient : myPatientsList) {
                        if (patient.getPatientFullName().contains(userQuery)) {
                            userQueryPatientList.add(patient);
                        }

                        if (userQueryPatientList.isEmpty()) {
                            rvPatientList.setVisibility(View.GONE);
                            tvEmptyMyPatientList.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyMyPatientList.setVisibility(View.GONE);
                            rvPatientList.setVisibility(View.VISIBLE);
                            doctorPatientListAdapter.refreshPatientList(userQueryPatientList);
                        }
                    }
                } else {
                    populateMyPatientsList();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onPatientClicked(View view, Patient patient) {
        Bundle currentPatientBundle = new Bundle();
        currentPatientBundle.putSerializable(Constants.CURRENT_PATIENT_KEY, patient);

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.patient_list_add_medication_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menuAddMedication) {
                    getParentFragmentManager().beginTransaction().replace(R.id.doctorFragmentContainer, DoctorAddMedicationFragment.class, currentPatientBundle).commit();
                    return true;
                } else return false;
            }
        });
    }
}