package com.kravitzsurf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kravitzsurf.R;
import com.kravitzsurf.models.User;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText, ageEditText;
    private Spinner genderSpinner;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        ageEditText = findViewById(R.id.ageEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        
        // Setup gender spinner
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_dropdown_item, genders);
        genderSpinner.setAdapter(adapter);
        
        registerButton.setOnClickListener(v -> registerUser());
    }
    
    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String ageText = ageEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }
        
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }
        
        if (TextUtils.isEmpty(ageText)) {
            ageEditText.setError("Age is required");
            return;
        }
        
        int age = Integer.parseInt(ageText);
        
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);
        
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        
                        User user = new User(name, email, age, gender);
                        
                        mDatabase.child("users").child(userId).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    progressBar.setVisibility(View.GONE);
                                    registerButton.setEnabled(true);
                                    
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, 
                                            "Registration successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, 
                                            "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        registerButton.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, 
                            "Registration failed: " + task.getException().getMessage(), 
                            Toast.LENGTH_LONG).show();
                    }
                });
    }
}
