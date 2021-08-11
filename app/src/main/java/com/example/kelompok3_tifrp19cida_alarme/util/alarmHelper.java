package com.example.kelompok3_tifrp19cida_alarme.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.example.kelompok3_tifrp19cida_alarme.R;

public class alarmHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock pm_wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reminder:PowerManagerAlarm");

//        Toast.makeText(context, "Ring ring!", Toast.LENGTH_LONG).show();
        //Vibrate
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(500, 10));
        //Notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notifChannel = new NotificationChannel("alarmeReminder", "Alarme!", NotificationManager.IMPORTANCE_HIGH);
        notifManager.createNotificationChannel(notifChannel);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, "alarmeReminder")
                .setSmallIcon(R.drawable.ic_icon_simple_color)
                .setContentTitle(intent.getStringExtra("Title"))
                .setContentText(intent.getStringExtra("Desc"))
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notifManager.notify(0, notifBuilder.build());
        //Delete reminder
        dbHelper db = new dbHelper(context);
        db.deleteData(intent.getIntExtra("id", 0));
        //Refresh


        if(pm_wl.isHeld()) {
            pm_wl.release();
        }
    }

    public void setAlarm(Context context, Long timestamp, String title, String desc, int id){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, alarmHelper.class);
        i.putExtra("Title", title);
        i.putExtra("Desc", desc);
        i.putExtra("id", id);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, timestamp, pi);
    }
}
