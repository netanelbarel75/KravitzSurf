package com.kravitzsurf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kravitzsurf.R;
import com.kravitzsurf.adapters.EnrolledClassAdapter;
import com.kravitzsurf.models.SurfClass;
import com.kravitzsurf.models.User;
import com.kravitzsurf.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements EnrolledClassAdapter.OnClassClickListener {
    
    private TextView nameTextView, emailTextView, ageTextView, genderTextView;
    private TextView enrolledClassesLabel;
    private RecyclerView enrolledClassesRecyclerView;
    private ProgressBar progressBar;
    private Button logoutButton;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EnrolledClassAdapter enrolledClassAdapter;
    private List<SurfClass> enrolledClasses;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        mAuth = FirebaseAuth.getInstance();
        
        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            // User not logged in, go back to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        preferenceManager = new PreferenceManager(this);
        
        initViews();
        loadUserProfile();
        loadEnrolledClasses();
    }
    
    private void initViews() {
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        ageTextView = findViewById(R.id.ageTextView);
        genderTextView = findViewById(R.id.genderTextView);
        enrolledClassesLabel = findViewById(R.id.enrolledClassesLabel);
        enrolledClassesRecyclerView = findViewById(R.id.enrolledClassesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        logoutButton = findViewById(R.id.logoutButton);
        
        enrolledClasses = new ArrayList<>();
        enrolledClassAdapter = new EnrolledClassAdapter(this, enrolledClasses, this);
        
        enrolledClassesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enrolledClassesRecyclerView.setAdapter(enrolledClassAdapter);
        
        logoutButton.setOnClickListener(v -> logout());
    }
    
    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        
        if (mAuth.getCurrentUser() == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        
        mDatabase.child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            updateUserInfo(user);
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, 
                            R.string.failed_to_load_profile, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void updateUserInfo(User user) {
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        ageTextView.setText(String.valueOf(user.getAge()));
        genderTextView.setText(user.getGender());
    }
    
    private void loadEnrolledClasses() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        
        mDatabase.child("users").child(userId).child("enrolledClasses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        enrolledClasses.clear();
                        int classCount = 0;
                        
                        for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                            String classId = classSnapshot.getKey();
                            classCount++;
                            loadClassDetails(classId);
                        }
                        
                        if (classCount == 0) {
                            enrolledClassesLabel.setText(R.string.no_enrolled_classes);
                        } else {
                            enrolledClassesLabel.setText(getString(R.string.enrolled_classes_count, classCount));
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, 
                            R.string.failed_to_load_enrolled_classes, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void loadClassDetails(String classId) {
        mDatabase.child("classes").child(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SurfClass surfClass = dataSnapshot.getValue(SurfClass.class);
                        if (surfClass != null) {
                            surfClass.setId(classId);
                            enrolledClasses.add(surfClass);
                            enrolledClassAdapter.notifyDataSetChanged();
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        mAuth.signOut();
        preferenceManager.clearAll();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
