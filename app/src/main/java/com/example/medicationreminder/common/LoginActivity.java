package com.example.medicationreminder.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.medicationreminder.R;
import com.example.medicationreminder.admin.AdminDashboardActivity;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.doctor.DoctorDashboardActivity;
import com.example.medicationreminder.patient.PatientDashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    Button btnLogin, btnRegister;
    TextInputLayout txtFieldEmail, txtFieldPassword;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initEvents();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        txtFieldEmail = findViewById(R.id.txtFieldEmail);
        txtFieldPassword = findViewById(R.id.txtFieldPassword);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initEvents() {
        btnLogin.setOnClickListener(view -> {
            String emailEntry = txtFieldEmail.getEditText().getText().toString().trim();
            String passwordEntry = txtFieldPassword.getEditText().getText().toString().trim();

            if (TextUtils.isEmpty(emailEntry)) {
                txtFieldPassword.setErrorEnabled(false);
                txtFieldEmail.setErrorEnabled(true);
                txtFieldEmail.setError("Empty Email!");

            } else if (TextUtils.isEmpty(passwordEntry)) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPassword.setErrorEnabled(true);
                txtFieldPassword.setError("Empty Password!");

            } else if (emailEntry.equals(Constants.ADMIN_EMAIL) && passwordEntry.equals(Constants.ADMIN_PASSWORD)) {
                txtFieldEmail.setErrorEnabled(false);
                txtFieldPassword.setErrorEnabled(false);

                Toast.makeText(LoginActivity.this, "Login As Admin", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();

            } else {
                firebaseAuth.signInWithEmailAndPassword(emailEntry, passwordEntry)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    txtFieldEmail.setErrorEnabled(false);
                                    txtFieldPassword.setErrorEnabled(false);

                                    if (emailEntry.contains(Constants.DOCTOR_EMAIL_SUFFIX)) {
                                        Toast.makeText(LoginActivity.this, "Login As Doctor", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(LoginActivity.this, DoctorDashboardActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login As Patient", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });
    }
}