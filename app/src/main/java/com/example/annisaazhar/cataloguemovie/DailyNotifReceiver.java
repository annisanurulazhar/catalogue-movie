package com.example.annisaazhar.cataloguemovie;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.Calendar;

public class DailyNotifReceiver extends BroadcastReceiver {

    public static final String TYPE_REMINDER = "Reminder";
    public static final String TYPE_RELEASE = "Release";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_TYPE = "type";
    private final int NOTIF_ID_REMINDER = 100;
    private final int NOTIF_ID_RELEASE = 101;

    public DailyNotifReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(EXTRA_TYPE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        String title = type.equalsIgnoreCase(TYPE_REMINDER) ? "Reminder" : "Release";
        int notifId = type.equalsIgnoreCase(TYPE_REMINDER) ? NOTIF_ID_REMINDER: NOTIF_ID_RELEASE;

        showNotification(context, title, message, notifId);
    }

    private void showNotification(Context mContext, String title, String message, int notifId) {
        Intent intent = new Intent(mContext, MainActivity.class);

        NotificationManager notificationManagerCompat = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, notifId, intent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_movie_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(mContext, android.R.color.transparent))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Integer.toString(notifId),
                    "NOTIFY_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.LTGRAY);

            builder.setChannelId(Integer.toString(notifId));
            notificationManagerCompat.createNotificationChannel(notificationChannel);
        }
        notificationManagerCompat.notify(notifId, builder.build());
    }

    public void setNotification(Context mContext, String type, String time, String message) {
        cancelAlarm(mContext, type);
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, DailyNotifReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_TYPE, type);
        int requestCode;
        if (type == TYPE_REMINDER) {
            requestCode = NOTIF_ID_REMINDER;
        } else {
            requestCode = NOTIF_ID_RELEASE;
        }
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String timeArray[] = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis() + 1, pendingIntent);
        }
        Toast.makeText(mContext, "Repeating alarm set up", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(Context context, String type){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyNotifReceiver.class);
        int requestCode = type.equalsIgnoreCase(TYPE_REMINDER) ? NOTIF_ID_REMINDER : NOTIF_ID_RELEASE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
