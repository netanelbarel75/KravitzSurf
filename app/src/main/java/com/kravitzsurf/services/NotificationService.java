package com.kravitzsurf.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.kravitzsurf.R;
import com.kravitzsurf.activities.ClassDetailsActivity;

public class NotificationService extends Service {
    
    private static final String CHANNEL_ID = "class_reminder_channel";
    private static final int NOTIFICATION_ID = 1;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String classId = intent.getStringExtra("class_id");
        String classTitle = intent.getStringExtra("class_title");
        String classTime = intent.getStringExtra("class_time");
        
        showNotification(classId, classTitle, classTime);
        
        stopSelf();
        return START_NOT_STICKY;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Class Reminders";
            String description = "Notifications for upcoming surf classes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private void showNotification(String classId, String classTitle, String classTime) {
        Intent intent = new Intent(this, ClassDetailsActivity.class);
        intent.putExtra("class_id", classId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Upcoming Surf Class")
                .setContentText(classTitle + " starts at " + classTime)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND);
        
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
