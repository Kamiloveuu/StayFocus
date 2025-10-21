package com.example.stayfocus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setupNavigation();
        setupHomeButton();
    }

    private void setupNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void setupHomeButton() {
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}