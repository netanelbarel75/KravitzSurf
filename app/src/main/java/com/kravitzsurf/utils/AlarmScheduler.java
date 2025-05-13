package com.kravitzsurf.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.kravitzsurf.models.SurfClass;
import com.kravitzsurf.receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmScheduler {
    
    private static final long REMINDER_BEFORE_CLASS = 30 * 60 * 1000; // 30 minutes before class
    private Context context;
    private AlarmManager alarmManager;
    
    public AlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
    
    public void scheduleClassReminder(SurfClass surfClass) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("class_id", surfClass.getId());
        intent.putExtra("class_title", surfClass.getTitle());
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String classTime = sdf.format(new Date(surfClass.getDateTime()));
        intent.putExtra("class_time", classTime);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                surfClass.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        long triggerTime = surfClass.getDateTime() - REMINDER_BEFORE_CLASS;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }
    
    public void cancelClassReminder(String classId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                classId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.cancel(pendingIntent);
    }
}
