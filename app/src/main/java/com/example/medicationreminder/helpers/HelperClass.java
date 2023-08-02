package com.example.medicationreminder.helpers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.medicationreminder.R;

import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HelperClass {

    public static int referenceMedicationType(String type) {
        switch (type) {
            case Constants.TYPE_LIQUID:
                return R.drawable.ic_liquid;
            case Constants.TYPE_SYRINGE:
                return R.drawable.ic_syringe;
            case Constants.TYPE_DROPS:
                return R.drawable.ic_drops;
            case Constants.TYPE_OINTMENT:
                return R.drawable.ic_ointment;
            default:
                return R.drawable.ic_capsule;
        }
    }

    public static String referenceDailyOrWeekly(boolean isDailyBased) {
        if (isDailyBased)
            return Constants.FREQUENCY_DAILY;
        else
            return Constants.FREQUENCY_WEEKLY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate parseDate(String date) {
        java.time.format.DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy", Locale.US);
        return LocalDate.parse(date, formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatLocalDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy", Locale.US);
        return localDate.format(formatter);
    }

    public static String formatDate(int year, int month, int day) {
        return day + "-" + month + "-" + year;
    }

    public static String formatCurrentDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy", Locale.US);
        return sdf.format(date);
    }

    public static String formatTime(int hour, int minute) {
        Date date = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalTime localTime = LocalTime.of(hour, minute);
            Instant instant = localTime.atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            date = Date.from(instant);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        if (date != null)
            return sdf.format(date);
        else
            return null;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Rangoon"));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
