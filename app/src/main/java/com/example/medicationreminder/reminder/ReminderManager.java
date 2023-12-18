package com.example.medicationreminder.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.model.Medication;
import com.example.medicationreminder.model.ReminderTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReminderManager {

    public static void startReminder(Context context, ReminderTime reminderTime, Medication medication) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            Date reminderDate = formatter.parse(reminderTime.getTimeDescription());

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));
            calendar.setTimeInMillis(System.currentTimeMillis());

            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));
            assert reminderDate != null;
            calendar2.setTime(reminderDate);

            int hour = calendar2.get(Calendar.HOUR_OF_DAY);
            int minute = calendar2.get(Calendar.MINUTE);

            Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            intent.putExtra(Constants.MEDICATION_TIME, reminderTime.getTimeDescription());
            intent.putExtra(Constants.MEDICATION_NAME, medication.getMedicationName());
            intent.putExtra(Constants.MEDICATION_DOSE, medication.getDose());
            intent.putExtra(Constants.MEDICATION_UNIT, medication.getUnit());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    reminderTime.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

//            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));
//            calendar2.add(Calendar.MINUTE, 1);
//
//            if (calendar2.getTimeInMillis() - calendar.getTimeInMillis() > 0)
//                calendar.add(Calendar.DATE, 1);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopReminder(Context context, ReminderTime reminderTime) {
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                reminderTime.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}
