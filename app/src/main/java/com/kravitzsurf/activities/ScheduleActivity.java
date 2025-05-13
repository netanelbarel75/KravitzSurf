package com.kravitzsurf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kravitzsurf.R;
import com.kravitzsurf.adapters.ClassAdapter;
import com.kravitzsurf.models.SurfClass;
import com.kravitzsurf.services.NotificationService;
import com.kravitzsurf.utils.AlarmScheduler;
import com.kravitzsurf.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements ClassAdapter.OnClassClickListener {
    
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ClassAdapter classAdapter;
    private List<SurfClass> classList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String selectedClassType = "group";
    private AlarmScheduler alarmScheduler;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
        getSupportActionBar().setTitle("Class Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        alarmScheduler = new AlarmScheduler(this);
        preferenceManager = new PreferenceManager(this);
        
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        
        classList = new ArrayList<>();
        classAdapter = new ClassAdapter(this, classList, this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(classAdapter);
        
        setupTabs();
        loadClasses(selectedClassType);
    }
    
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Group Classes"));
        tabLayout.addTab(tabLayout.newTab().setText("Private Classes"));
        tabLayout.addTab(tabLayout.newTab().setText("Parent & Child"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        selectedClassType = "group";
                        break;
                    case 1:
                        selectedClassType = "private";
                        break;
                    case 2:
                        selectedClassType = "parent_child";
                        break;
                }
                loadClasses(selectedClassType);
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void loadClasses(String classType) {
        progressBar.setVisibility(View.VISIBLE);
        
        mDatabase.child("classes")
                .orderByChild("type")
                .equalTo(classType)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        classList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SurfClass surfClass = snapshot.getValue(SurfClass.class);
                            if (surfClass != null) {
                                surfClass.setId(snapshot.getKey());
                                classList.add(surfClass);
                            }
                        }
                        classAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ScheduleActivity.this, 
                            "Failed to load classes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    @Override
    public void onClassClick(SurfClass surfClass) {
        Intent intent = new Intent(this, ClassDetailsActivity.class);
        intent.putExtra("class_id", surfClass.getId());
        startActivity(intent);
    }
    
    @Override
    public void onEnrollClick(SurfClass surfClass) {
        String userId = mAuth.getCurrentUser().getUid();
        
        // Add user to enrolled list for the class
        mDatabase.child("enrollments").child(surfClass.getId()).child(userId).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // Update user's enrolled classes
                    mDatabase.child("users").child(userId).child("enrolledClasses")
                            .child(surfClass.getId()).setValue(true);
                    
                    // Schedule notification
                    alarmScheduler.scheduleClassReminder(surfClass);
                    
                    // Save as next class if it's the closest upcoming
                    updateNextClass(surfClass);
                    
                    Toast.makeText(ScheduleActivity.this, 
                        "Enrolled successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ScheduleActivity.this, 
                        "Enrollment failed", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void updateNextClass(SurfClass enrolledClass) {
        String currentNextClassId = preferenceManager.getNextClassId();
        if (currentNextClassId == null) {
            preferenceManager.setNextClassId(enrolledClass.getId());
            preferenceManager.setNextClassTime(enrolledClass.getDateTime());
        } else {
            long currentNextTime = preferenceManager.getNextClassTime();
            if (enrolledClass.getDateTime() < currentNextTime) {
                preferenceManager.setNextClassId(enrolledClass.getId());
                preferenceManager.setNextClassTime(enrolledClass.getDateTime());
            }
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
