package com.kravitzsurf.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kravitzsurf.R;

public class SimpleProfileActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_profile);
        
        TextView testTextView = findViewById(R.id.testTextView);
        testTextView.setText("Profile is working!");
    }
}
