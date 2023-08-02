package com.example.medicationreminder.patient;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.adapter.PatientTodayListAdapter;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.helpers.HelperClass;
import com.example.medicationreminder.model.Medication;
import com.example.medicationreminder.model.ReminderTime;
import com.example.medicationreminder.reminder.ReminderManager;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientTodayListFragment extends Fragment {

    private static final String TAG = "PatientTodayListFragment";

    Context context;
    HorizontalPicker picker;
    Calendar calendar;
    FloatingActionButton fabMedicationList;
    RecyclerView rvTodayList;
    ArrayList<Medication> medicationList;
    ArrayList<ReminderTime> medicationWithReminderTimesList;
    HashMap<ReminderTime, Medication> reminderTimeToMedicationHashMap;
    HashMap<ReminderTime, Medication> reminderTimeToMedicationHashMapForToday;
    PatientTodayListAdapter patientTodayListAdapter;
    TextView tvEmptyMedicationList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference patientDatabaseRef;

    public PatientTodayListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_today_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        retrieveMedicationList();
        initEvents();
        setUpDayScrollDatePicker();
    }

    private void initViews(View view) {
        picker = view.findViewById(R.id.picker);
        calendar = Calendar.getInstance(Locale.US);

        fabMedicationList = view.findViewById(R.id.fabMedicationList);
        rvTodayList = view.findViewById(R.id.rvTodayList);

        tvEmptyMedicationList = view.findViewById(R.id.tvEmptyMedicationList);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        patientDatabaseRef = firebaseDatabase.getReference(Constants.PATH_PATIENTS);

        medicationList = new ArrayList<>();
        medicationWithReminderTimesList = new ArrayList<>();

        reminderTimeToMedicationHashMap = new HashMap<>();
        reminderTimeToMedicationHashMapForToday = new HashMap<>();
    }

    private void retrieveMedicationList() {
        medicationList.clear();

        String patientId = "Pat_" + firebaseUser.getUid();

        patientDatabaseRef.child(patientId).child(Constants.PATH_PATIENT_MEDICATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Medication medication = childSnapshot.getValue(Medication.class);
                        medicationList.add(medication);
                    }

                    Log.d(TAG, "Medication List: " + medicationList.size());
                }

//                if (!medicationList.isEmpty()) {
//                    for (Medication medication : medicationList) {
//                        medicationIdToReminderTimesMap.put(medication.getMedicationId(), medication.getSelectedReminderTimes());
//                    }
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    populateTodayList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTodayList() {
        reminderTimeToMedicationHashMapForToday.clear();

        Date date = new Date();
        Date todayDate;

        String todayDateAsString = HelperClass.formatCurrentDate(date);

        LocalDate todayDateAsLocalDate = HelperClass.parseDate(todayDateAsString);
        todayDate = HelperClass.getDate(todayDateAsLocalDate.getYear(), todayDateAsLocalDate.getMonthValue(), todayDateAsLocalDate.getDayOfMonth());

        Log.d(TAG, "Today's Date: " + date);
        Log.d(TAG, "Medication List: " + medicationList);

        if (!medicationList.isEmpty()) {
            for (Medication medication : medicationList) {

                java.time.LocalDate startLocalDate;

                startLocalDate = HelperClass.parseDate(medication.getStartDate());
                Date startDate = HelperClass.getDate(startLocalDate.getYear(), startLocalDate.getMonthValue(), startLocalDate.getDayOfMonth());

                Log.d(TAG, "Medication Start Date: " + startDate);

                if (medication.getHasEndDate() && medication.getEndDate() != null) {

                    java.time.LocalDate endLocalDate = HelperClass.parseDate(medication.getEndDate());
                    Date endDate = HelperClass.getDate(endLocalDate.getYear(), endLocalDate.getMonthValue(), endLocalDate.getDayOfMonth());

                    Log.d(TAG, "Medication End Date: " + endDate);

                    if (!(todayDate.before(startDate) || todayDate.after(endDate))) {

                        if (medication.getSelectedReminderTimes().size() > 1) {
                            for (Map.Entry<String, ReminderTime> entry : medication.getSelectedReminderTimes().entrySet()) {
                                medicationWithReminderTimesList.add(entry.getValue());
                                reminderTimeToMedicationHashMap.put(entry.getValue(), medication);

                                reminderTimeToMedicationHashMapForToday.put(entry.getValue(), medication);
                            }
                        }

                        for (Map.Entry<ReminderTime, Medication> entry : reminderTimeToMedicationHashMapForToday.entrySet()) {
                            ReminderManager.startReminder(context, entry.getKey(), entry.getValue());
                            Log.d(TAG, "Reminder Time: " + entry.getKey().getTimeDescription());
                        }
                    }
                }
            }

            Log.d(TAG, "Medication with reminder times count: " + medicationWithReminderTimesList.size());
            Log.d(TAG, "Reminder time to medication hashmap count: " + reminderTimeToMedicationHashMap.size());

            if (!medicationWithReminderTimesList.isEmpty()) {
                if (getActivity() != null && isAdded()) {
                    tvEmptyMedicationList.setVisibility(View.GONE);
                    rvTodayList.setVisibility(View.VISIBLE);

                    patientTodayListAdapter = new PatientTodayListAdapter(getActivity(), medicationWithReminderTimesList, reminderTimeToMedicationHashMap);
                    rvTodayList.setAdapter(patientTodayListAdapter);
                    rvTodayList.setLayoutManager(new LinearLayoutManager(getActivity()));
                }

            } else {
                tvEmptyMedicationList.setVisibility(View.VISIBLE);
                rvTodayList.setVisibility(View.GONE);
            }
        }
    }

    private void setUpDayScrollDatePicker() {

//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//        int monthOfYear = calendar.get(Calendar.MONTH) + 1;
//        int year = calendar.get(Calendar.YEAR);

        picker.setListener(new DatePickerListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(DateTime dateSelected) {
                medicationWithReminderTimesList.clear();
                reminderTimeToMedicationHashMap.clear();

                if (dateSelected != null) {

                    int year = dateSelected.getYear();
                    int month = dateSelected.getMonthOfYear();
                    int day = dateSelected.getDayOfMonth();

                    Date selectedDate = HelperClass.getDate(year, month, day);

                    if (!medicationList.isEmpty()) {
                        for (Medication medication : medicationList) {
                            Log.d(TAG, "Medication start date: " + medication.getStartDate());

                            java.time.LocalDate startLocalDate = HelperClass.parseDate(medication.getStartDate());
                            Date startDate = HelperClass.getDate(startLocalDate.getYear(), startLocalDate.getMonthValue(), startLocalDate.getDayOfMonth());

                            if (medication.getHasEndDate() && medication.getEndDate() != null) {

                                java.time.LocalDate endLocalDate = HelperClass.parseDate(medication.getEndDate());
                                Date endDate = HelperClass.getDate(endLocalDate.getYear(), endLocalDate.getMonthValue(), endLocalDate.getDayOfMonth());

                                if (!(selectedDate.before(startDate) || selectedDate.after(endDate))) {

                                    if (medication.getSelectedReminderTimes().size() > 1) {
                                        for (Map.Entry<String, ReminderTime> entry : medication.getSelectedReminderTimes().entrySet()) {
                                            medicationWithReminderTimesList.add(entry.getValue());
                                            reminderTimeToMedicationHashMap.put(entry.getValue(), medication);
                                        }


                                    }
                                }
                            }
                        }

                        Log.d(TAG, "Medication with reminder times count: " + medicationWithReminderTimesList.size());
                        Log.d(TAG, "Reminder time to medication hashmap count: " + reminderTimeToMedicationHashMap.size());

                        if (!medicationWithReminderTimesList.isEmpty()) {
                            if (getActivity() != null && isAdded()) {
                                tvEmptyMedicationList.setVisibility(View.GONE);
                                rvTodayList.setVisibility(View.VISIBLE);

                                patientTodayListAdapter = new PatientTodayListAdapter(getActivity(), medicationWithReminderTimesList, reminderTimeToMedicationHashMap);
                                rvTodayList.setAdapter(patientTodayListAdapter);
                                rvTodayList.setLayoutManager(new LinearLayoutManager(getActivity()));
                            }

                        } else {
                            tvEmptyMedicationList.setVisibility(View.VISIBLE);
                            rvTodayList.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        if (getActivity() != null && isAdded()) {
            picker.setDays(14)
                    .setOffset(3)
                    .setDateSelectedColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                    .setDateSelectedTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVariant))
                    .setMonthAndYearTextColor(ContextCompat.getColor(getActivity(), R.color.colorVariant))
                    .setTodayButtonTextColor(ContextCompat.getColor(getActivity(), R.color.colorVariant))
                    .setTodayDateTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVariant))
                    .setTodayDateBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green))
                    .setUnselectedDayTextColor(ContextCompat.getColor(getActivity(), R.color.colorVariant))
                    .setDayOfWeekTextColor(ContextCompat.getColor(getActivity(), R.color.colorVariant))
                    .showTodayButton(true)
                    .init();

            picker.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryVariant));
            picker.setDate(new DateTime());
        }
    }

    private void initEvents() {
        fabMedicationList.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction().replace(R.id.patientFragmentContainer, PatientMedicationListFragment.class, null).commit();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}