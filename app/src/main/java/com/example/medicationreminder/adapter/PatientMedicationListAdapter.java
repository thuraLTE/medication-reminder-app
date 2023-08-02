package com.example.medicationreminder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.HelperClass;
import com.example.medicationreminder.model.Day;
import com.example.medicationreminder.model.Medication;
import com.example.medicationreminder.model.ReminderTime;

import java.util.List;
import java.util.Map;

public class PatientMedicationListAdapter extends RecyclerView.Adapter<PatientMedicationListAdapter.PatientMedicationItemViewHolder> {

    Context context;
    List<Medication> medicationList;

    public PatientMedicationListAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public PatientMedicationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_patient_medication_item, parent, false);
        return new PatientMedicationItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientMedicationItemViewHolder holder, int position) {
        Medication currentMedication = medicationList.get(position);

        holder.tvName.setText(currentMedication.getMedicationName());
        holder.ivType.setImageResource(HelperClass.referenceMedicationType(currentMedication.getType()));
        holder.tvDose.setText(String.valueOf(currentMedication.getDose()) + " ");
        holder.tvUnit.setText(currentMedication.getUnit());

        holder.tvFrequency.setText(context.getString(
                R.string.frequency_placeholder,
                currentMedication.getSelectedReminderTimes().size(),
                HelperClass.referenceDailyOrWeekly(currentMedication.isDailyBased())));

        if (!currentMedication.isDailyBased() && currentMedication.getSelectedDaysOfWeek() != null && !currentMedication.getSelectedDaysOfWeek().isEmpty()) {
            holder.tvDays.setVisibility(View.VISIBLE);

            StringBuilder daysBuilder = new StringBuilder();
            for (Map.Entry<String, Day> entry : currentMedication.getSelectedDaysOfWeek().entrySet()) {
                daysBuilder.append(entry.getValue().getDayDescription()).append(" . ");
            }

            holder.tvDays.setText(daysBuilder.toString());
        }

        StringBuilder reminderTimesBuilder = new StringBuilder();
        for (Map.Entry<String, ReminderTime> entry : currentMedication.getSelectedReminderTimes().entrySet()) {
            reminderTimesBuilder.append(entry.getValue().getTimeDescription()).append(" . ");
        }
        holder.tvTime.setText(reminderTimesBuilder);

        if (currentMedication.getHasEndDate() && currentMedication.getEndDate() != null)
            holder.tvDuration.setText(context.getString(R.string.end_date_placeholder, currentMedication.getEndDate()));
        else
            holder.tvDuration.setText(context.getString(R.string.no_end_date));

        if (!TextUtils.isEmpty(currentMedication.getIntakeInstructions())) {
            holder.linearIntakeAdvice.setVisibility(View.VISIBLE);
            holder.ivIntakeAdvice.setVisibility(View.VISIBLE);
            holder.tvIntakeAdvice.setText(currentMedication.getIntakeInstructions());
        } else {
            holder.ivIntakeAdvice.setVisibility(View.GONE);
            holder.linearIntakeAdvice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    static class PatientMedicationItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivType, ivIntakeAdvice;
        TextView tvName, tvDose, tvUnit, tvFrequency, tvDays, tvTime, tvDuration, tvIntakeAdvice;
        LinearLayout linearIntakeAdvice;

        public PatientMedicationItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ivType = itemView.findViewById(R.id.ivType);
            ivIntakeAdvice = itemView.findViewById(R.id.ivIntakeAdvice);

            tvName = itemView.findViewById(R.id.tvName);
            tvDose = itemView.findViewById(R.id.tvDose);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvFrequency = itemView.findViewById(R.id.tvFrequency);
            tvDays = itemView.findViewById(R.id.tvDays);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvIntakeAdvice = itemView.findViewById(R.id.tvIntakeAdvice);

            linearIntakeAdvice = itemView.findViewById(R.id.linearIntakeAdvice);
        }
    }
}
