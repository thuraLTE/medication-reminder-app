package com.example.medicationreminder.patient;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.helpers.HelperClass;
import com.example.medicationreminder.model.Appointment;
import com.example.medicationreminder.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class PatientMyDoctorFragment extends Fragment {

    private static final String TAG = "PatientMyDoctorFragment";

    TextView tvMyDoctorName, tvMyDoctorId, tvMyDoctorEmail, tvMyDoctorPhNum, tvMyDoctorSpecialization, tvMyDoctorQualification, tvEmptySubscribedDoctor;
    Button btnAddAppointment, btnConfirm;
    ScrollView scrollSubscribedDoctor;
    LinearLayout linearAddAppointmentTime;
    ArrayList<Appointment> appointmentList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference patientDatabaseRef;
    DatabaseReference doctorDatabaseRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String currentPatientId;
    Doctor connDoctor;

    public PatientMyDoctorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_my_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        checkSubscribedDoctor();
        initEvents();
    }

    private void initViews(View view) {
        tvMyDoctorName = view.findViewById(R.id.tvMyDoctorName);
        tvMyDoctorId = view.findViewById(R.id.tvMyDoctorId);
        tvMyDoctorEmail = view.findViewById(R.id.tvMyDoctorEmail);
        tvMyDoctorPhNum = view.findViewById(R.id.tvMyDoctorPhNum);
        tvMyDoctorSpecialization = view.findViewById(R.id.tvMyDoctorSpecialization);
        tvMyDoctorQualification = view.findViewById(R.id.tvMyDoctorQaulification);
        tvEmptySubscribedDoctor = view.findViewById(R.id.tvEmptySubscribedDoctor);

        btnAddAppointment = view.findViewById(R.id.btnAddAppointment);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        scrollSubscribedDoctor = view.findViewById(R.id.scrollSubscribedDoctor);

        linearAddAppointmentTime = view.findViewById(R.id.linearAddAppointmentTime);

        firebaseDatabase = FirebaseDatabase.getInstance();
        patientDatabaseRef = firebaseDatabase.getReference(Constants.PATH_PATIENTS);
        doctorDatabaseRef = firebaseDatabase.getReference(Constants.PATH_DOCTORS);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        appointmentList = new ArrayList<>();

        assert firebaseUser != null;
        currentPatientId = "Pat_" + firebaseUser.getUid();
    }

    private void checkSubscribedDoctor() {
        patientDatabaseRef.child(currentPatientId).child("connDoctor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                connDoctor = snapshot.getValue(Doctor.class);

                if (connDoctor != null) {
                    tvEmptySubscribedDoctor.setVisibility(View.GONE);
                    scrollSubscribedDoctor.setVisibility(View.VISIBLE);

                    tvMyDoctorName.setText(connDoctor.getDoctorFullName());
                    tvMyDoctorId.setText(connDoctor.getDoctorId());
                    tvMyDoctorEmail.setText(connDoctor.getDoctorEmail());
                    tvMyDoctorPhNum.setText(connDoctor.getDoctorPhNum());
                    tvMyDoctorSpecialization.setText(connDoctor.getDoctorSpecialization());
                    tvMyDoctorQualification.setText(connDoctor.getDoctorQualification());

                } else {
                    tvEmptySubscribedDoctor.setVisibility(View.VISIBLE);
                    scrollSubscribedDoctor.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initEvents() {
        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!appointmentList.isEmpty()) {
                    for (int i=0; i<appointmentList.size(); i++) {
                        Appointment currentAppointment = appointmentList.get(i);

                        String newAppointmentId = "App_" + patientDatabaseRef.child(currentPatientId).child(Constants.PATH_PATIENT_APPOINTMENTS).push().getKey();

                        currentAppointment.setAppointmentId(newAppointmentId);

                        if (currentAppointment.getAppointmentId() != null) {
                            patientDatabaseRef.child(currentPatientId).child(Constants.PATH_PATIENT_APPOINTMENTS).child(currentAppointment.getAppointmentId()).setValue(currentAppointment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (getActivity() != null && isAdded()) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "New appointment added successfully", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(getActivity(), "Failure to add appointment", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            doctorDatabaseRef.child(connDoctor.getDoctorId()).child(Constants.PATH_CONN_PATIENTS).child(currentPatientId).child(Constants.PATH_PATIENT_APPOINTMENTS).child(currentAppointment.getAppointmentId()).setValue(currentAppointment);

                            getParentFragmentManager().beginTransaction().replace(R.id.patientFragmentContainer, PatientDoctorListFragment.class, null).commit();
                        }
                    }
                }
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        RelativeLayout relativeAddAppointmentDateAndTime = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_add_appointment_date_and_time, null, false);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                linearAddAppointmentTime.addView(relativeAddAppointmentDateAndTime);

                ImageView ivDeleteAppointment = relativeAddAppointmentDateAndTime.findViewById(R.id.ivDeleteAppointment);
                TextView tvAppointmentDate = relativeAddAppointmentDateAndTime.findViewById(R.id.tvAppointmentDate);
                TextView tvAppointmentTime = relativeAddAppointmentDateAndTime.findViewById(R.id.tvAppointmentTime);

                String date = HelperClass.formatDate(year, month + 1, day);
                tvAppointmentDate.setText(date);

                showTimePickerDialog(relativeAddAppointmentDateAndTime, ivDeleteAppointment, date, tvAppointmentTime);
            }
        }, year, month, day);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_NEGATIVE) {
                    relativeAddAppointmentDateAndTime.setVisibility(View.GONE);
                    dialogInterface.dismiss();
                }
            }
        });

        datePickerDialog.show();
    }

    private void showTimePickerDialog(RelativeLayout relativeAddAppointmentDateAndTime, ImageView ivDeleteAppointment, String date, TextView tvAppointmentTime) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = HelperClass.formatTime(hour, minute);

                tvAppointmentTime.setText(time);

                Appointment newAppointment = new Appointment(null, date, time);
                appointmentList.add(newAppointment);

                ivDeleteAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        appointmentList.remove(newAppointment);

                        relativeAddAppointmentDateAndTime.setVisibility(View.GONE);
                    }
                });
            }
        }, hour, minute, false);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_NEGATIVE) {
                    relativeAddAppointmentDateAndTime.setVisibility(View.GONE);
                    dialogInterface.dismiss();
                }
            }
        });

        timePickerDialog.show();
    }
}