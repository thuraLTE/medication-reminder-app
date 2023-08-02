package com.example.medicationreminder.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.common.LoginActivity;
import com.example.medicationreminder.helpers.Constants;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorDashboardActivity extends AppCompatActivity {

    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navDrawer;
    DrawerLayout dLayout;
    ImageView ivLogout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference doctorDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        initViews();
        initEvents();
        setUpNavigationDrawer();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolBar);
        navDrawer = findViewById(R.id.navDrawer);
        dLayout = findViewById(R.id.dLayout);
        ivLogout = findViewById(R.id.ivLogout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        putCurrentDoctorDataOnNavigationDrawerHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.doctorFragmentContainer, DoctorAppointmentListFragment.class, null).commit();
    }

    private void initEvents() {
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    firebaseAuth.signOut();

                    Intent intent = new Intent(DoctorDashboardActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void putCurrentDoctorDataOnNavigationDrawerHeader() {
        String currentDoctorId = "Doc_" + firebaseUser.getUid();

        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);
        doctorDatabaseRef.child(currentDoctorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("doctorFullName").getValue(String.class);
                String email = snapshot.child("doctorEmail").getValue(String.class);

                View headerView = navDrawer.getHeaderView(0);
                TextView tvHeaderDoctorName = headerView.findViewById(R.id.tvUserFullName);
                TextView tvHeaderDoctorEmail = headerView.findViewById(R.id.tvUserEmail);

                tvHeaderDoctorName.setText(name);
                tvHeaderDoctorEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUpNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        dLayout.addDrawerListener(toggle);

        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.fragmentDoctorAppointmentList) {
                    getSupportActionBar().setTitle(R.string.appointments);
                    getSupportFragmentManager().beginTransaction().replace(R.id.doctorFragmentContainer, DoctorAppointmentListFragment.class, null).commit();
                    dLayout.closeDrawers();
                    return true;
                } else if (item.getItemId() == R.id.fragmentDoctorMyPatientList) {
                    getSupportActionBar().setTitle(R.string.patients);
                    getSupportFragmentManager().beginTransaction().replace(R.id.doctorFragmentContainer, DoctorMyPatientListFragment.class, null).commit();
                    dLayout.closeDrawers();
                    return true;
                } else
                    return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}