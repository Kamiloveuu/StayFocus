package com.example.stayfocus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnSoundSettings, btnTimerSettings ;
    private Button btnHome ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupNavigation();
        setupSettingsButtons();
        setupHomeButton();
    }

    private void initializeViews() {
        btnSoundSettings = findViewById(R.id.btnSoundSettings);
        btnTimerSettings = findViewById(R.id.btnTimerSettings);

        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);
    }

    private void setupNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void setupSettingsButtons() {
        btnSoundSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SoundSettingsActivity.class);
            startActivity(intent);
        });

        btnTimerSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, TimerSettingsActivity.class);
            startActivity(intent);
        });
    }

    private void setupHomeButton() {
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}