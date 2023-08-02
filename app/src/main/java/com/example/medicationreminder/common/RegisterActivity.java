package com.example.medicationreminder.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Doctor;
import com.example.medicationreminder.model.Patient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    Button btnRegister;
    ImageView ivBack;
    Spinner spinnerDoctorSpecialization, spinnerDoctorQualification;
    TextInputLayout txtFieldFullName, txtFieldEmail, txtFieldPhNum, txtFieldDoctorQualification, txtFieldPassword, txtFieldConfirmPassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    String doctorSpecialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        attachTextWatcherToEmailField();
        setUpDoctorSpecializationSpinner();
        initEvents();
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btnRegister);

        txtFieldFullName = findViewById(R.id.txtFieldFullName);
        txtFieldEmail = findViewById(R.id.txtFieldEmail);
        txtFieldPhNum = findViewById(R.id.txtFieldPhNum);
        txtFieldDoctorQualification = findViewById(R.id.txtFieldDoctorQualification);
        txtFieldPassword = findViewById(R.id.txtFieldPassword);
        txtFieldConfirmPassword = findViewById(R.id.txtFieldConfirmPassword);

        ivBack = findViewById(R.id.ivBack);

        spinnerDoctorSpecialization = findViewById(R.id.spinnerDoctorSpecialization);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setUpDoctorSpecializationSpinner() {
        ArrayAdapter<CharSequence> spinnerDoctorSpecializationAdapter = ArrayAdapter.createFromResource(this, R.array.doctor_category_array, R.layout.spinner_item_two);
        spinnerDoctorSpecializationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_two);

        spinnerDoctorSpecialization.setAdapter(spinnerDoctorSpecializationAdapter);
        spinnerDoctorSpecialization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 doctorSpecialization = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initEvents() {
        btnRegister.setOnClickListener(view -> {

            String fullName = txtFieldFullName.getEditText().getText().toString().trim();
            String email = txtFieldEmail.getEditText().getText().toString().trim();
            String phNum = txtFieldPhNum.getEditText().getText().toString().trim();
            String qualification = txtFieldDoctorQualification.getEditText().getText().toString().trim();
            String password = txtFieldPassword.getEditText().getText().toString().trim();
            String confirmPassword = txtFieldConfirmPassword.getEditText().getText().toString().trim();

            if (TextUtils.isEmpty(fullName)) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPhNum.setErrorEnabled(false);
                txtFieldPassword.setErrorEnabled(false);
                txtFieldConfirmPassword.setErrorEnabled(false);

                txtFieldFullName.setErrorEnabled(true);
                txtFieldFullName.setError("Empty Name!");

            } else if (TextUtils.isEmpty(email)) {
                txtFieldPassword.setErrorEnabled(false);
                txtFieldConfirmPassword.setErrorEnabled(false);
                txtFieldFullName.setErrorEnabled(false);
                txtFieldPhNum.setErrorEnabled(false);

                txtFieldEmail.setErrorEnabled(true);
                txtFieldEmail.setError("Empty Email!");

            } else if (TextUtils.isEmpty(phNum)) {
                txtFieldPassword.setErrorEnabled(false);
                txtFieldConfirmPassword.setErrorEnabled(false);
                txtFieldFullName.setErrorEnabled(false);
                txtFieldEmail.setErrorEnabled(false);

                txtFieldPhNum.setErrorEnabled(true);
                txtFieldPhNum.setError("Empty Phone Number!");

            } else if (TextUtils.isEmpty(password)) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPhNum.setErrorEnabled(false);
                txtFieldFullName.setErrorEnabled(false);
                txtFieldConfirmPassword.setErrorEnabled(false);

                txtFieldPassword.setErrorEnabled(true);
                txtFieldPassword.setError("Empty Password!");

            } else if (password.length() < 6) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPhNum.setErrorEnabled(false);
                txtFieldConfirmPassword.setErrorEnabled(false);
                txtFieldFullName.setErrorEnabled(false);

                txtFieldPassword.setErrorEnabled(true);
                txtFieldPassword.setError("Password must be at least 6 characters!");

            } else if (TextUtils.isEmpty(confirmPassword)) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPhNum.setErrorEnabled(false);
                txtFieldFullName.setErrorEnabled(false);
                txtFieldPassword.setErrorEnabled(false);

                txtFieldConfirmPassword.setErrorEnabled(true);
                txtFieldConfirmPassword.setError("Empty Password!");

            } else if (!password.equals(confirmPassword)) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPhNum.setErrorEnabled(false);
                txtFieldPassword.setErrorEnabled(false);
                txtFieldFullName.setErrorEnabled(false);

                txtFieldConfirmPassword.setErrorEnabled(true);
                txtFieldConfirmPassword.setError("Passwords Unmatched!");

            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email: " + email + ", Password: " + password);

                                txtFieldEmail.setErrorEnabled(false);
                                txtFieldPhNum.setErrorEnabled(false);
                                txtFieldPassword.setErrorEnabled(false);
                                txtFieldFullName.setErrorEnabled(false);
                                txtFieldConfirmPassword.setErrorEnabled(false);

                                firebaseUser = firebaseAuth.getCurrentUser();
                                Log.d(TAG, "Current user uid: " + firebaseUser.getUid());

                                saveUserToDB(fullName, email, phNum, qualification);

                            } else {
                                Log.d(TAG, task.getException().toString());
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attachTextWatcherToEmailField() {
        Objects.requireNonNull(txtFieldEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userInput = charSequence.toString();

                if (userInput.contains(Constants.DOCTOR_EMAIL_SUFFIX)) {
                    spinnerDoctorSpecialization.setVisibility(View.VISIBLE);
                    txtFieldDoctorQualification.setVisibility(View.VISIBLE);
                } else {
                    spinnerDoctorSpecialization.setVisibility(View.GONE);
                    txtFieldDoctorQualification.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void saveUserToDB(String fullName, String email, String phNum, String qualification) {
        if (email.contains(Constants.DOCTOR_EMAIL_SUFFIX)) {
            String newDoctorId = "Doc_" + firebaseUser.getUid();
            DatabaseReference newDoctorDatabaseRef = firebaseDatabase.getReference().child(Constants.PATH_DOCTORS).child(newDoctorId);

            Doctor doctor = new Doctor(newDoctorId, fullName, email, phNum, doctorSpecialization, qualification, null);
            newDoctorDatabaseRef.setValue(doctor).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Current Doctor Id: " + doctor.getDoctorId());

                    Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    Log.e(TAG, "Failure to add new doctor");
            });

        } else {
            String newPatientId = "Pat_" + firebaseUser.getUid();
            DatabaseReference newPatientDatabaseReference = firebaseDatabase.getReference().child(Constants.PATH_PATIENTS).child(newPatientId);

            Patient patient = new Patient(newPatientId, fullName, email, phNum, null, null, null, null);
            newPatientDatabaseReference.setValue(patient).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Current Patient Id: " + patient.getPatientId());

                    Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    Log.e(TAG, "Failure to add new patient");
            });
        }
    }
}