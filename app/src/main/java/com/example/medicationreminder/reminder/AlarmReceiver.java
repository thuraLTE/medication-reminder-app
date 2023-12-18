package com.example.medicationreminder.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.medicationreminder.R;
import com.example.medicationreminder.helpers.Constants;
import com.example.medicationreminder.patient.PatientDashboardActivity;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int PENDING_INTENT_REQUEST_CODE = 102;
    public static final int NOTIFICATION_ID = 103;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager.class
        );

        Intent contentIntent = new Intent(context.getApplicationContext(), PatientDashboardActivity.class);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                PENDING_INTENT_REQUEST_CODE,
                contentIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), Constants.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title_placeholder, intent.getStringExtra(Constants.MEDICATION_TIME)))
                .setContentText(context.getString(R.string.notification_text_placeholder, intent.getStringExtra(Constants.MEDICATION_NAME), intent.getIntExtra(Constants.MEDICATION_DOSE, 1), intent.getStringExtra(Constants.MEDICATION_UNIT)))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);

        NotificationChannel channel = new NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
}
