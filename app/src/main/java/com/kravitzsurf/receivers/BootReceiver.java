package com.kravitzsurf.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kravitzsurf.models.SurfClass;
import com.kravitzsurf.utils.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            rescheduleAlarms(context);
        }
    }
    
    private void rescheduleAlarms(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        AlarmScheduler alarmScheduler = new AlarmScheduler(context);
        
        // Get user's enrolled classes and reschedule reminders
        database.child("users").child(userId).child("enrolledClasses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                            String classId = classSnapshot.getKey();
                            loadClassAndScheduleReminder(database, classId, alarmScheduler);
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    
    private void loadClassAndScheduleReminder(DatabaseReference database, String classId, AlarmScheduler alarmScheduler) {
        database.child("classes").child(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SurfClass surfClass = dataSnapshot.getValue(SurfClass.class);
                        if (surfClass != null) {
                            surfClass.setId(classId);
                            // Only reschedule if the class is in the future
                            if (surfClass.getDateTime() > System.currentTimeMillis()) {
                                alarmScheduler.scheduleClassReminder(surfClass);
                            }
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
