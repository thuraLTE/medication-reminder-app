package com.example.medicationreminder.doctor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.helpers.HelperClass;
import com.example.medicationreminder.model.Day;
import com.example.medicationreminder.model.Medication;
import com.example.medicationreminder.model.Patient;
import com.example.medicationreminder.model.ReminderTime;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DoctorAddMedicationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    Context context;
    EditText edtMedicationName, edtManualEntry;
    Spinner spinnerFrequency, spinnerDuration, spinnerIntakeInstructions;
    RelativeLayout relativeDose, relativeStartDate, relativeDurationOrEndDate, relativeAddReminderTime, relativeDayPicker;
    LinearLayout linearAddReminderTime;
    TextView tvDoseInput, tvDoseUnit, tvStartDateInput, tvDurationOrEndDate, tvDurationOrEndDateInput, tvAddReminderTime, tvTime1;
    ImageView ivCapsule, ivLiquid, ivSyringe, ivDrop, ivOintment, ivAddTime1;
    View divider2, divider3;
    CheckBox cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun;
    Button btnConfirm;
    ArrayList<RelativeLayout> relativeAddReminderTimesList;
    ArrayList<String> selectedDayList;
    ArrayList<String> selectedReminderTimeList;
    List<ImageView> typeList;
    String intakeInstructionsChoice = "";
    String selectedType = "";
    String selectedTypeUnit = "";
    int selectedDose = 0;
    boolean isDailyBased = true;
    String startDateAsString = "";
    LocalDate startDate;
    Boolean hasEndDate = false;
    String endDateAsString = "";
    int amountOfDays = 0;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference doctorDatabaseRef;
    DatabaseReference patientDatabaseRef;
    Patient currentPatient;

    public DoctorAddMedicationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_add_medication, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setUpSpinners();
        initEvents();
    }

    private void initViews(View view) {
        edtMedicationName = view.findViewById(R.id.edtMedicationName);
        edtManualEntry = view.findViewById(R.id.edtManualEntry);

        ivCapsule = view.findViewById(R.id.ivCapsule);
        ivLiquid = view.findViewById(R.id.ivLiquid);
        ivSyringe = view.findViewById(R.id.ivSyringe);
        ivDrop = view.findViewById(R.id.ivDrop);
        ivOintment = view.findViewById(R.id.ivOintment);
        typeList = Arrays.asList(ivCapsule, ivLiquid, ivSyringe, ivDrop, ivOintment);
        selectedTypeUnit = getString(R.string.capsule_unit);

        relativeDose = view.findViewById(R.id.relativeDose);
        tvDoseInput = view.findViewById(R.id.tvDoseInput);
        tvDoseUnit = view.findViewById(R.id.tvDoseUnit);

        spinnerFrequency = view.findViewById(R.id.spinnerFrequency);
        spinnerDuration = view.findViewById(R.id.spinnerDuration);
        spinnerIntakeInstructions = view.findViewById(R.id.spinnerIntakeInstructions);

        relativeStartDate = view.findViewById(R.id.relativeStartDate);
        tvStartDateInput = view.findViewById(R.id.tvStartDateInput);

        relativeDurationOrEndDate = view.findViewById(R.id.relativeDurationOrEndDate);
        tvDurationOrEndDate = view.findViewById(R.id.tvDurationOrEndDate);
        tvDurationOrEndDateInput = view.findViewById(R.id.tvDurationOrEndDateInput);

        relativeAddReminderTime = view.findViewById(R.id.relativeAddReminderTime);
        linearAddReminderTime = view.findViewById(R.id.linearAddReminderTime);
        tvAddReminderTime = view.findViewById(R.id.tvAddReminderTime);
        tvTime1 = view.findViewById(R.id.tvTime1);
        ivAddTime1 = view.findViewById(R.id.ivAddTime1);

        selectedReminderTimeList = new ArrayList<>();

        divider2 = view.findViewById(R.id.divider2);
        divider3 = view.findViewById(R.id.divider3);

        relativeDayPicker = view.findViewById(R.id.relativeDayPicker);
        cbMon = view.findViewById(R.id.cbMon);
        cbTue = view.findViewById(R.id.cbTue);
        cbWed = view.findViewById(R.id.cbWed);
        cbThu = view.findViewById(R.id.cbThu);
        cbFri = view.findViewById(R.id.cbFri);
        cbSat = view.findViewById(R.id.cbSat);
        cbSun = view.findViewById(R.id.cbSun);

        relativeAddReminderTimesList = new ArrayList<>();
        selectedDayList = new ArrayList<>();

        btnConfirm = view.findViewById(R.id.btnConfirm);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        doctorDatabaseRef = database.getReference(Constants.PATH_DOCTORS);
        patientDatabaseRef = database.getReference(Constants.PATH_PATIENTS);

        if (getArguments() != null)
            currentPatient = (Patient) getArguments().getSerializable(Constants.CURRENT_PATIENT_KEY);
    }

    private void setUpSpinners() {
        setUpTypePicker();
        setUpDosePicker();
        setUpStartDatePicker();
        setUpFrequencySpinner();
        setUpDurationSpinner();
        setUpIntakeInstructionsSpinner();
    }

    private void setUpTypePicker() {
        changeActiveInactiveBackgroundState(ivCapsule);
        selectedType = Constants.TYPE_CAPSULE;
        selectedTypeUnit = "capsule(s)";
        tvDoseUnit.setText(selectedTypeUnit);

        ivCapsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActiveInactiveBackgroundState(ivCapsule);
                selectedType = Constants.TYPE_CAPSULE;
                selectedTypeUnit = "capsule(s)";
                tvDoseUnit.setText(selectedTypeUnit);
            }
        });
        ivLiquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActiveInactiveBackgroundState(ivLiquid);
                selectedType = Constants.TYPE_LIQUID;
                selectedTypeUnit = "tablespoon(s)";
                tvDoseUnit.setText(selectedTypeUnit);
            }
        });
        ivSyringe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActiveInactiveBackgroundState(ivSyringe);
                selectedType = Constants.TYPE_SYRINGE;
                selectedTypeUnit = "injection(s)";
                tvDoseUnit.setText(selectedTypeUnit);
            }
        });
        ivDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActiveInactiveBackgroundState(ivDrop);
                selectedType = Constants.TYPE_DROPS;
                selectedTypeUnit = "drop(s)";
                tvDoseUnit.setText(selectedTypeUnit);
            }
        });
        ivOintment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActiveInactiveBackgroundState(ivOintment);
                selectedType = Constants.TYPE_OINTMENT;
                selectedTypeUnit = "fingertip(s)";
                tvDoseUnit.setText(selectedTypeUnit);
            }
        });
    }

    private void changeActiveInactiveBackgroundState(ImageView activeType) {
        for (int i=0; i<(typeList.size()); i++) {
            if (!typeList.get(i).equals(activeType))
                typeList.get(i).setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.medication_type_icon_bg_inactive));
            else
                typeList.get(i).setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.medication_type_icon_bg_active));
        }
    }

    private void setUpDosePicker() {
        tvDoseInput.setText("1");
        selectedDose = 1;

        if (selectedTypeUnit != null) {

            relativeDose.setOnClickListener(view -> {
                Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
                dialog.setTitle(requireContext().getString(R.string.unit_enquiry_placeholder, selectedTypeUnit));
                dialog.setContentView(R.layout.number_picker_dialogue);
                dialog.setCancelable(false);

                NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
                Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(100);
                numberPicker.setWrapSelectorWheel(false);

                btnConfirm.setOnClickListener(view1 -> {
                    selectedDose = numberPicker.getValue();
                    tvDoseInput.setText(String.valueOf(numberPicker.getValue()));
                    dialog.dismiss();
                });

                btnCancel.setOnClickListener(view2 -> {
                    dialog.dismiss();
                });

                dialog.show();
            });
        }
    }

    private void setUpStartDatePicker() {
        Date date = new Date();
        startDateAsString = HelperClass.formatCurrentDate(date);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        tvStartDateInput.setText(startDateAsString);

        relativeStartDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));

            int year= calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    startDateAsString = HelperClass.formatDate(year, month + 1, day);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startDate = LocalDate.of(year, month + 1, day);
                    }

                    tvStartDateInput.setText(HelperClass.formatDate(year, month + 1, day));
                }
            }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void setUpFrequencySpinner() {
        ArrayAdapter<CharSequence> spinnerFrequencyAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.frequency_array, R.layout.spinner_item_one);
        spinnerFrequencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_one);

        spinnerFrequency.setAdapter(spinnerFrequencyAdapter);

        spinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    isDailyBased = true;

                    relativeAddReminderTimesList.add(relativeAddReminderTime);

                    linearAddReminderTime.setVisibility(View.VISIBLE);
                    relativeAddReminderTime.setVisibility(View.VISIBLE);

                    relativeDayPicker.setVisibility(View.GONE);

                    setUpTimePickerDialog(relativeAddReminderTime, tvAddReminderTime, tvTime1, ivAddTime1, linearAddReminderTime);
                } else if (i == 2) {
                    isDailyBased = false;

                    relativeDayPicker.setVisibility(View.VISIBLE);

                    cbMon.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);
                    cbTue.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);
                    cbWed.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);
                    cbThu.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);
                    cbFri.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);
                    cbSat.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);
                    cbSun.setOnCheckedChangeListener(DoctorAddMedicationFragment.this);

                    relativeAddReminderTimesList.add(relativeAddReminderTime);

                    linearAddReminderTime.setVisibility(View.VISIBLE);
                    relativeAddReminderTime.setVisibility(View.VISIBLE);

                    setUpTimePickerDialog(relativeAddReminderTime, tvAddReminderTime, tvTime1, ivAddTime1, linearAddReminderTime);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpDurationSpinner() {
        ArrayAdapter<CharSequence> spinnerDurationAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.duration_array, R.layout.spinner_item_one);
        spinnerDurationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_one);

        spinnerDuration.setAdapter(spinnerDurationAdapter);

        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    hasEndDate = false;

                    relativeDurationOrEndDate.setVisibility(View.GONE);
                    divider2.setVisibility(View.GONE);

                } else if (i == 1) {
                    hasEndDate = true;

                    relativeDurationOrEndDate.setVisibility(View.VISIBLE);
                    divider2.setVisibility(View.VISIBLE);
                    tvDurationOrEndDate.setText(R.string.end_date);

                    setUpEndDateSpinner();
                } else {
                    hasEndDate = true;

                    relativeDurationOrEndDate.setVisibility(View.VISIBLE);
                    divider2.setVisibility(View.VISIBLE);
                    tvDurationOrEndDate.setText(R.string.duration);

                    setUpDurationDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpIntakeInstructionsSpinner() {
        ArrayAdapter<CharSequence> spinnerIntakeInstructionsAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.intake_instructions_array, R.layout.spinner_item_one);
        spinnerIntakeInstructionsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_one);

        spinnerIntakeInstructions.setAdapter(spinnerIntakeInstructionsAdapter);

        spinnerIntakeInstructions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    edtManualEntry.setVisibility(View.GONE);
                    intakeInstructionsChoice = "";
                }
                else if (i == 1) {
                    edtManualEntry.setVisibility(View.GONE);
                    intakeInstructionsChoice = adapterView.getItemAtPosition(i).toString();
                }
                else if (i == 2) {
                    edtManualEntry.setVisibility(View.GONE);
                    intakeInstructionsChoice = adapterView.getItemAtPosition(i).toString();
                }
                else if (i == 3) {
                    edtManualEntry.setVisibility(View.GONE);
                    intakeInstructionsChoice = adapterView.getItemAtPosition(i).toString();
                }
                else {
                    edtManualEntry.setVisibility(View.VISIBLE);
                    intakeInstructionsChoice = edtManualEntry.getText().toString().trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpTimePickerDialog(RelativeLayout relativeAddReminderTime, TextView tvAddReminderTime, TextView tvAddedTime, ImageView ivAddTime, LinearLayout parent) {
        relativeAddReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String selectedReminderTime = HelperClass.formatTime(hour, minute);

                        selectedReminderTimeList.add(selectedReminderTime);

                        tvAddReminderTime.setText(R.string.remove_reminder_time);
                        tvAddedTime.setText(HelperClass.formatTime(hour, minute));
                        ivAddTime.setImageResource(R.drawable.ic_remove);
                        ivAddTime.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red), PorterDuff.Mode.SRC_IN);

                        ivAddTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectedReminderTimeList.remove(selectedReminderTime);

                                relativeAddReminderTimesList.remove(relativeAddReminderTime);

                                relativeAddReminderTime.setVisibility(View.GONE);
                            }
                        });

                        RelativeLayout relativeAddAnotherReminderTime = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_add_reminder_time, null, false);
                        parent.addView(relativeAddAnotherReminderTime);

                        TextView tvAddAnotherReminderTime = relativeAddAnotherReminderTime.findViewById(R.id.tvAddAnotherReminderTime);
                        TextView tvAddedAnotherTime = relativeAddAnotherReminderTime.findViewById(R.id.tvAddedAnotherTime);
                        ImageView ivAddAnotherTime = relativeAddAnotherReminderTime.findViewById(R.id.ivAddAnotherTime);

                        setUpTimePickerDialog(relativeAddAnotherReminderTime, tvAddAnotherReminderTime, tvAddedAnotherTime, ivAddAnotherTime, linearAddReminderTime);
                    }
                }, hour, minute, false);

                timePickerDialog.show();
            }
        });
    }

    private void setUpEndDateSpinner() {
        Date date = new Date();
        tvDurationOrEndDateInput.setText(HelperClass.formatCurrentDate(date));

        relativeDurationOrEndDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));

            int year= calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    endDateAsString = HelperClass.formatDate(year, month + 1, day);

                    tvDurationOrEndDateInput.setText(HelperClass.formatDate(year, month + 1, day));
                }
            }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void setUpDurationDialog() {
        tvDurationOrEndDateInput.setText(requireContext().getString(R.string.day_unit_placeholder, "1"));

        relativeDurationOrEndDate.setOnClickListener(view -> {
            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setTitle(R.string.day_duration_enquiry);
            dialog.setContentView(R.layout.number_picker_dialogue);
            dialog.setCancelable(false);

            NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
            Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);

            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(100);
            numberPicker.setWrapSelectorWheel(false);

            btnConfirm.setOnClickListener(view1 -> {
                amountOfDays = numberPicker.getValue();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate endDateAsLocalDate = startDate.plusDays(amountOfDays).minusDays(1);

                    endDateAsString = HelperClass.formatLocalDate(endDateAsLocalDate);
                }

                tvDurationOrEndDateInput.setText(requireContext().getString(R.string.day_unit_placeholder, String.valueOf(numberPicker.getValue())));
                dialog.dismiss();
            });

            btnCancel.setOnClickListener(view2 -> {
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    private void initEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String doctorId = "Doc_" + firebaseUser.getUid();
                String connPatientId = currentPatient.getPatientId();

                DatabaseReference newMedicationRef = doctorDatabaseRef.child(doctorId).child(Constants.PATH_CONN_PATIENTS).child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).push();

                String newMedicationId = "Med_" + newMedicationRef.getKey();
                String newMedicationName = edtMedicationName.getText().toString().trim();

                if (!hasEndDate)
                    endDateAsString = null;

                Medication newMedication = new Medication(
                        newMedicationId,
                        newMedicationName,
                        selectedType,
                        selectedDose,
                        selectedTypeUnit,
                        isDailyBased,
                        null,
                        null,
                        startDateAsString,
                        hasEndDate,
                        endDateAsString,
                        intakeInstructionsChoice);

                patientDatabaseRef.child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).setValue(newMedication).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(context, "Medication Added Successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Failed to Add Medication", Toast.LENGTH_SHORT).show();
                    }
                });

                doctorDatabaseRef.child(doctorId).child(Constants.PATH_CONN_PATIENTS).child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).setValue(newMedication).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(context, "Medication Added Successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Failed to Add Medication", Toast.LENGTH_SHORT).show();
                    }
                });

                if (!selectedReminderTimeList.isEmpty()) {
                    for (String selectedReminderTime : selectedReminderTimeList) {
                        DatabaseReference newReminderTimeDatabaseRef = patientDatabaseRef.child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).child(Constants.PATH_SELECTED_REMINDER_TIMES).push();

                        String newReminderTimeId = "Rem_" + newReminderTimeDatabaseRef.getKey();

                        ReminderTime newReminderTime = new ReminderTime(newReminderTimeId, selectedReminderTime);

                        patientDatabaseRef.child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).child(Constants.PATH_SELECTED_REMINDER_TIMES).child(newReminderTimeId).setValue(newReminderTime);

                        doctorDatabaseRef.child(doctorId).child(Constants.PATH_CONN_PATIENTS).child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).child(Constants.PATH_SELECTED_REMINDER_TIMES).child(newReminderTimeId).setValue(newReminderTime);
                    }
                }

                if (!selectedDayList.isEmpty()) {
                    for (String selectedDay : selectedDayList) {
                        DatabaseReference newDayDatabaseRef = patientDatabaseRef.child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).child(Constants.PATH_SELECTED_DAYS).push();

                        String newDayId = "Day_" + newDayDatabaseRef.getKey();

                        Day newDay = new Day(newDayId, selectedDay);

                        patientDatabaseRef.child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).child(Constants.PATH_SELECTED_DAYS).child(newDayId).setValue(newDay);

                        doctorDatabaseRef.child(doctorId).child(Constants.PATH_CONN_PATIENTS).child(connPatientId).child(Constants.PATH_PATIENT_MEDICATIONS).child(newMedicationId).child(Constants.PATH_SELECTED_DAYS).child(newDayId).setValue(newDay);
                    }
                }

                getParentFragmentManager().beginTransaction().replace(R.id.doctorFragmentContainer, DoctorMyPatientListFragment.class, null).commit();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.cbMon) {
            String mon = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(mon);
            } else {
                selectedDayList.remove(mon);
            }
        } else if (compoundButton.getId() == R.id.cbTue) {
            String tue = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(tue);
            } else {
                selectedDayList.remove(tue);
            }
        } else if (compoundButton.getId() == R.id.cbWed) {
            String wed = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(wed);
            } else {
                selectedDayList.remove(wed);
            }
        } else if (compoundButton.getId() == R.id.cbThu) {
            String thu = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(thu);
            } else {
                selectedDayList.remove(thu);
            }
        } else if (compoundButton.getId() == R.id.cbFri) {
            String fri = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(fri);
            } else {
                selectedDayList.remove(fri);
            }
        } else if (compoundButton.getId() == R.id.cbSat) {
            String sat = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(sat);
            } else {
                selectedDayList.remove(sat);
            }
        } else if (compoundButton.getId() == R.id.cbSun) {
            String sun = compoundButton.getText().toString();
            if (isChecked) {
                selectedDayList.add(sun);
            } else {
                selectedDayList.remove(sun);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}