package com.kravitzsurf.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kravitzsurf.R;
import com.kravitzsurf.models.SurfClass;
import com.kravitzsurf.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private CardView scheduleCard, weatherCard, profileCard;
    private TextView welcomeTextView, nextClassTextView;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        preferenceManager = new PreferenceManager(this);
        
        scheduleCard = findViewById(R.id.scheduleCard);
        weatherCard = findViewById(R.id.weatherCard);
        profileCard = findViewById(R.id.profileCard);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        nextClassTextView = findViewById(R.id.nextClassTextView);
        logoutButton = findViewById(R.id.logoutButton);
        
        loadUserName();
        loadNextClass();
        setupClickListeners();
    }
    
    private void loadUserName() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        welcomeTextView.setText(getString(R.string.welcome_user, name));
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        welcomeTextView.setText("Welcome!");
                    }
                });
    }
    
    private void loadNextClass() {
        String nextClassId = preferenceManager.getNextClassId();
        if (nextClassId != null) {
            mDatabase.child("classes").child(nextClassId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SurfClass surfClass = dataSnapshot.getValue(SurfClass.class);
                            if (surfClass != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                                String dateStr = sdf.format(new Date(surfClass.getDateTime()));
                                nextClassTextView.setText("Next Class: " + surfClass.getTitle() + " - " + dateStr);
                            }
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            nextClassTextView.setText("No upcoming classes");
                        }
                    });
        } else {
            nextClassTextView.setText("No upcoming classes scheduled");
        }
    }
    
    private void setupClickListeners() {
        scheduleCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
        });
        
        weatherCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WeatherActivity.class));
        });
        
        profileCard.setOnClickListener(v -> {
            // Implement profile activity
        });
        
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            preferenceManager.clearAll();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            mAuth.signOut();
            preferenceManager.clearAll();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
