package com.example.medicationreminder.admin;

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

public class AdminDashboardActivity extends AppCompatActivity {

    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navDrawer;
    DrawerLayout dLayout;
    ImageView ivLogout;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

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

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        putAdminDataOnNavigationDrawerHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainer, AdminPatientListFragment.class, null).commit();
    }

    private void putAdminDataOnNavigationDrawerHeader() {
        View headerView = navDrawer.getHeaderView(0);
        TextView tvHeaderAdminName = headerView.findViewById(R.id.tvUserFullName);
        TextView tvHeaderAdminEmail = headerView.findViewById(R.id.tvUserEmail);

        tvHeaderAdminName.setText("Admin");
        tvHeaderAdminEmail.setText(Constants.ADMIN_EMAIL);
    }

    private void setUpNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        dLayout.addDrawerListener(toggle);

        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.fragmentAdminPatientList) {
                    getSupportActionBar().setTitle(R.string.patients);
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainer, AdminPatientListFragment.class, null).commit();
                    dLayout.closeDrawers();
                    return true;
                } else if (item.getItemId() == R.id.fragmentAdminDoctorList) {
                    getSupportActionBar().setTitle(R.string.doctors);
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainer, AdminDoctorListFragment.class, null).commit();
                    dLayout.closeDrawers();
                    return true;
                } else
                    return false;
            }
        });
    }

    private void initEvents() {
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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