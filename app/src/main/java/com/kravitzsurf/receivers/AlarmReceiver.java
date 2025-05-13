package com.kravitzsurf.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kravitzsurf.services.NotificationService;

public class AlarmReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String classId = intent.getStringExtra("class_id");
        String classTitle = intent.getStringExtra("class_title");
        String classTime = intent.getStringExtra("class_time");
        
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("class_id", classId);
        serviceIntent.putExtra("class_title", classTitle);
        serviceIntent.putExtra("class_time", classTime);
        
        context.startService(serviceIntent);
    }
}
