package com.example.stayfocus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etActivityName;
    private Button btnStart, btnStop;
    private TextView tvTimer;
    private SharedPreferences prefs;

    // Timer default values
    private long workTime = 25 * 60 * 1000; // 25 menit
    private long shortBreakTime = 5 * 60 * 1000; // 5 menit
    private long longBreakTime = 15 * 60 * 1000; // 15 menit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        loadTimerSettings();
        setupNavigation();
        setupTimerButtons();
    }

    private void initializeViews() {
        etActivityName = findViewById(R.id.etActivityName);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        tvTimer = findViewById(R.id.tvTimer);

        prefs = getSharedPreferences("StayFocusPrefs", MODE_PRIVATE);
    }

    private void loadTimerSettings() {
        // Load saved timer settings
        workTime = prefs.getLong("work_time", 25 * 60 * 1000);
        shortBreakTime = prefs.getLong("short_break_time", 5 * 60 * 1000);
        longBreakTime = prefs.getLong("long_break_time", 15 * 60 * 1000);

        // Update timer display
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        int minutes = (int) (workTime / 1000) / 60;
        int seconds = (int) (workTime / 1000) % 60;
        String formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(formatted);
    }

    private void setupNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void setupTimerButtons() {
        btnStart.setOnClickListener(v -> {
            String activityName = etActivityName.getText().toString().trim();
            if (activityName.isEmpty()) {
                etActivityName.setError("Enter activity name");
                return;
            }

            // Save last activity
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_activity", activityName);
            editor.apply();

            // PERBAIKAN: Langsung buka TimerActivity, bukan TimerSettingsActivity
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            intent.putExtra("activity_name", activityName);
            intent.putExtra("work_time", workTime);
            intent.putExtra("short_break_time", shortBreakTime);
            intent.putExtra("long_break_time", longBreakTime);
            startActivity(intent);
        });

        btnStop.setOnClickListener(v -> {
            // Reset to default
            etActivityName.setText("");
            loadTimerSettings();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimerSettings();

        // Show last activity if exists
        String lastActivity = prefs.getString("last_activity", "");
        if (!lastActivity.isEmpty()) {
            etActivityName.setText(lastActivity);
        }
    }
}