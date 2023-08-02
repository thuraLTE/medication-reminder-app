package com.example.medicationreminder.patient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.common.LoginActivity;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.reminder.ReminderManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PatientDashboardActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView btmNavView;
    ImageView ivLogout;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        initViews();
        initEvents();
        setUpBottomNavigationView();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolBar);
        btmNavView = findViewById(R.id.btmNavView);
        ivLogout = findViewById(R.id.ivLogout);

        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.patientFragmentContainer, PatientTodayListFragment.class, null).commit();
    }

    private void initEvents() {
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    firebaseAuth.signOut();

                    Intent intent = new Intent(PatientDashboardActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void setUpBottomNavigationView() {
        btmNavView.setSelectedItemId(R.id.fragmentMedicationList);
        btmNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.fragmentMedicationList) {
                getSupportActionBar().setTitle(R.string.today);
                getSupportFragmentManager().beginTransaction().replace(R.id.patientFragmentContainer, PatientTodayListFragment.class, null).commit();
                return true;
            } else if (item.getItemId() == R.id.fragmentDoctorList) {
                getSupportActionBar().setTitle(R.string.doctors);
                getSupportFragmentManager().beginTransaction().replace(R.id.patientFragmentContainer, PatientDoctorListFragment.class, null).commit();
                return true;
            } else
                return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}