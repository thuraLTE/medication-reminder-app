package com.example.medicationreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.HelperClass;
import com.example.medicationreminder.model.Medication;
import com.example.medicationreminder.model.ReminderTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientTodayListAdapter extends RecyclerView.Adapter<PatientTodayListAdapter.PatientTodayItemViewHolder> {

    Context context;
    ArrayList<ReminderTime> medicationWithReminderTimeList;
    HashMap<ReminderTime, Medication> reminderTimeMedicationHashMap;

    public void refreshTodayList(List<ReminderTime> newMedicationWithReminderTimeList) {
        this.medicationWithReminderTimeList.clear();
        medicationWithReminderTimeList.addAll(newMedicationWithReminderTimeList);
        notifyDataSetChanged();
    }

    public PatientTodayListAdapter(Context context, ArrayList<ReminderTime> medicationWithReminderTimeList, HashMap<ReminderTime, Medication> reminderTimeMedicationHashMap) {
        this.context = context;
        this.medicationWithReminderTimeList = medicationWithReminderTimeList;
        this.reminderTimeMedicationHashMap = reminderTimeMedicationHashMap;
    }

    @NonNull
    @Override
    public PatientTodayItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_today_item, parent, false);
        return new PatientTodayItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientTodayItemViewHolder holder, int position) {
        ReminderTime currentReminderTime = medicationWithReminderTimeList.get(position);
        Medication currentMedication = reminderTimeMedicationHashMap.get(currentReminderTime);

        if (currentMedication.getType() != null)
            holder.ivType.setImageResource(HelperClass.referenceMedicationType(currentMedication.getType()));
        holder.tvName.setText(currentMedication.getMedicationName());
        holder.tvTime.setText(currentReminderTime.getTimeDescription());
        holder.tvDoseAndUnit.setText(currentMedication.getDose() + " " + currentMedication.getUnit());

    }

    @Override
    public int getItemCount() {
        return medicationWithReminderTimeList.size();
    }

    static class PatientTodayItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivType;
        TextView tvTime, tvName, tvDoseAndUnit;

        public PatientTodayItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ivType = itemView.findViewById(R.id.ivType);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvName = itemView.findViewById(R.id.tvName);
            tvDoseAndUnit = itemView.findViewById(R.id.tvDoseAndUnit);
        }
    }
}
