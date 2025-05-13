package com.kravitzsurf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.kravitzsurf.R;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 2000;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        mAuth = FirebaseAuth.getInstance();
        
        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView appNameTextView = findViewById(R.id.appNameTextView);
        
        // Add fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        logoImageView.startAnimation(fadeIn);
        appNameTextView.startAnimation(fadeIn);
        
        new Handler().postDelayed(() -> {
            Intent intent;
            if (mAuth.getCurrentUser() != null) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
