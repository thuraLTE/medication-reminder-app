package com.example.medicationreminder.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddNewDoctorFragment extends Fragment {

    Context context;
    EditText edtDoctorName, edtDoctorEmail, edtDoctorPhNum, edtDoctorQualification;
    Button btnConfirm;
    Spinner spinnerDoctorSpecialization;
    String doctorSpecialization = "";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference doctorDatabaseRef;

    public AdminAddNewDoctorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_add_new_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setUpDoctorSpecializationSpinner();
        initEvents();
    }

    private void initViews(View view) {
        edtDoctorName = view.findViewById(R.id.edtDoctorName);
        edtDoctorEmail = view.findViewById(R.id.edtDoctorEmail);
        edtDoctorPhNum = view.findViewById(R.id.edtDoctorPhNum);
        edtDoctorQualification = view.findViewById(R.id.edtDoctorQualification);

        spinnerDoctorSpecialization = view.findViewById(R.id.spinnerDoctorSpecialization);

        btnConfirm = view.findViewById(R.id.btnConfirm);

        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setUpDoctorSpecializationSpinner() {
        ArrayAdapter<CharSequence> spinnerDoctorSpecializationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.doctor_category_array, R.layout.spinner_item_one);
        spinnerDoctorSpecializationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_one);

        spinnerDoctorSpecialization.setAdapter(spinnerDoctorSpecializationAdapter);
        spinnerDoctorSpecialization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    doctorSpecialization = null;
                } else
                    doctorSpecialization = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String doctorName = edtDoctorName.getText().toString().trim();
                String doctorEmail = edtDoctorEmail.getText().toString().trim();
                String doctorPhNum = edtDoctorPhNum.getText().toString().trim();
                String doctorQualification = edtDoctorQualification.getText().toString().trim();

                if (TextUtils.isEmpty(doctorName))
                    Toast.makeText(context, "Empty Doctor Name!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(doctorEmail))
                    Toast.makeText(context, "Empty Doctor Email!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(doctorPhNum))
                    Toast.makeText(context, "Empty Doctor Phone Number!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(doctorQualification))
                    Toast.makeText(context, "Empty Doctor Qualification", Toast.LENGTH_SHORT).show();
                else if (!doctorEmail.contains(Constants.DOCTOR_EMAIL_SUFFIX)) {
                    Toast.makeText(context, "A doctor email must contain doctor@medicare.org", Toast.LENGTH_SHORT).show();
                }
                else {
                    addNewDoctor(doctorName, doctorEmail, doctorPhNum, doctorSpecialization, doctorQualification);
                }
            }
        });
    }

    private void addNewDoctor(String doctorName, String doctorEmail, String doctorPhNum, String doctorSpecialization, String doctorQualification) {
        doctorDatabaseRef = firebaseDatabase.getReference().child(Constants.PATH_DOCTORS);

        String newDoctorId = "Doc_" + doctorDatabaseRef.push().getKey();

        Doctor newDoctor = new Doctor(newDoctorId, doctorName, doctorEmail, doctorPhNum, doctorSpecialization, doctorQualification, null);
        doctorDatabaseRef.child(newDoctorId).setValue(newDoctor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "New Doctor Added Successfully", Toast.LENGTH_SHORT).show();

                    getParentFragmentManager().beginTransaction().replace(R.id.adminFragmentContainer, AdminDoctorListFragment.class, null).commit();
                }

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}