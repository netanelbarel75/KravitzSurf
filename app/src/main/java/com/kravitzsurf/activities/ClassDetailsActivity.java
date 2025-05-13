package com.kravitzsurf.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kravitzsurf.R;
import com.kravitzsurf.models.SurfClass;
import com.kravitzsurf.utils.AlarmScheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClassDetailsActivity extends AppCompatActivity {
    
    private TextView titleTextView, descriptionTextView, instructorTextView;
    private TextView dateTimeTextView, durationTextView, capacityTextView;
    private TextView enrolledCountTextView, typeTextView, priceTextView;
    private Button enrollButton;
    
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String classId;
    private SurfClass currentClass;
    private AlarmScheduler alarmScheduler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);
        
        getSupportActionBar().setTitle("Class Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        alarmScheduler = new AlarmScheduler(this);
        
        classId = getIntent().getStringExtra("class_id");
        
        initViews();
        loadClassDetails();
        checkEnrollmentStatus();
    }
    
    private void initViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        instructorTextView = findViewById(R.id.instructorTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        durationTextView = findViewById(R.id.durationTextView);
        capacityTextView = findViewById(R.id.capacityTextView);
        enrolledCountTextView = findViewById(R.id.enrolledCountTextView);
        typeTextView = findViewById(R.id.typeTextView);
        priceTextView = findViewById(R.id.priceTextView);
        enrollButton = findViewById(R.id.enrollButton);
        
        enrollButton.setOnClickListener(v -> handleEnrollment());
    }
    
    private void loadClassDetails() {
        mDatabase.child("classes").child(classId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentClass = dataSnapshot.getValue(SurfClass.class);
                        if (currentClass != null) {
                            currentClass.setId(classId);
                            updateUI();
                            countEnrolledStudents();
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ClassDetailsActivity.this, 
                            "Failed to load class details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void updateUI() {
        titleTextView.setText(currentClass.getTitle());
        descriptionTextView.setText(currentClass.getDescription());
        instructorTextView.setText("Instructor: " + currentClass.getInstructor());
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        dateTimeTextView.setText(sdf.format(new Date(currentClass.getDateTime())));
        
        durationTextView.setText("Duration: " + currentClass.getDuration() + " minutes");
        capacityTextView.setText("Capacity: " + currentClass.getCapacity() + " students");
        typeTextView.setText("Type: " + getTypeDisplayName(currentClass.getType()));
        priceTextView.setText("Price: $" + currentClass.getPrice());
    }
    
    private String getTypeDisplayName(String type) {
        switch (type) {
            case "group":
                return "Group Class";
            case "private":
                return "Private Class";
            case "parent_child":
                return "Parent & Child Class";
            default:
                return type;
        }
    }
    
    private void countEnrolledStudents() {
        mDatabase.child("enrollments").child(classId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        enrolledCountTextView.setText("Enrolled: " + count + "/" + currentClass.getCapacity());
                        
                        if (count >= currentClass.getCapacity()) {
                            enrollButton.setEnabled(false);
                            enrollButton.setText("Class Full");
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        enrolledCountTextView.setText("Enrolled: Unable to load");
                    }
                });
    }
    
    private void checkEnrollmentStatus() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("enrollments").child(classId).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            enrollButton.setText("Enrolled");
                            enrollButton.setEnabled(false);
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    
    private void handleEnrollment() {
        String userId = mAuth.getCurrentUser().getUid();
        
        // Add user to enrolled list for the class
        mDatabase.child("enrollments").child(classId).child(userId).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // Update user's enrolled classes
                    mDatabase.child("users").child(userId).child("enrolledClasses")
                            .child(classId).setValue(true);
                    
                    // Schedule notification
                    alarmScheduler.scheduleClassReminder(currentClass);
                    
                    Toast.makeText(ClassDetailsActivity.this, 
                        "Enrolled successfully", Toast.LENGTH_SHORT).show();
                    
                    enrollButton.setText("Enrolled");
                    enrollButton.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ClassDetailsActivity.this, 
                        "Enrollment failed", Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
