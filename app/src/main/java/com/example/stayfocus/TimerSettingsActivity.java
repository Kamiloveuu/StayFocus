package com.example.stayfocus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TimerSettingsActivity extends AppCompatActivity {

    private EditText etWork, etShortBreak, etLongBreak, etLoopCount;
    private TextView tvLoopInfo;
    private Button btnReset, btnSave;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_timer);

        initializeViews();
        setupNavigation();
        loadSettings();
        setupButtons();
        setupLoopListener();
    }

    private void initializeViews() {
        etWork = findViewById(R.id.etWork);
        etShortBreak = findViewById(R.id.etShortBreak);
        etLongBreak = findViewById(R.id.etLongBreak);
        etLoopCount = findViewById(R.id.etLoopCount);
        tvLoopInfo = findViewById(R.id.tvLoopInfo);
        btnReset = findViewById(R.id.btnReset);
        btnSave = findViewById(R.id.btnSave);

        prefs = getSharedPreferences("StayFocusPrefs", MODE_PRIVATE);
    }

    private void setupNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void loadSettings() {
        long workTime = prefs.getLong("work_time", 25 * 60 * 1000);
        long shortBreak = prefs.getLong("short_break_time", 5 * 60 * 1000);
        long longBreak = prefs.getLong("long_break_time", 15 * 60 * 1000);
        int loopCount = prefs.getInt("loop_count", 1);

        etWork.setText(String.valueOf(workTime / 60000));
        etShortBreak.setText(String.valueOf(shortBreak / 60000));
        etLongBreak.setText(String.valueOf(longBreak / 60000));
        etLoopCount.setText(String.valueOf(loopCount));

        updateLoopInfo(loopCount);
    }

    private void setupLoopListener() {
        etLoopCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int loopCount = Integer.parseInt(etLoopCount.getText().toString());
                    if (loopCount < 1) {
                        etLoopCount.setText("1");
                        loopCount = 1;
                    }
                    updateLoopInfo(loopCount);
                } catch (NumberFormatException e) {
                    etLoopCount.setText("1");
                    updateLoopInfo(1);
                }
            }
        });
    }

    private void updateLoopInfo(int loopCount) {
        int totalWorkSessions = loopCount * 4;
        String info = loopCount + " Loop = " + totalWorkSessions +
                " Work Sessions + " + loopCount + " Long Break";
        tvLoopInfo.setText(info);
    }

    private void setupButtons() {
        btnReset.setOnClickListener(v -> {
            etWork.setText("25");
            etShortBreak.setText("5");
            etLongBreak.setText("15");
            etLoopCount.setText("1");
            updateLoopInfo(1);
        });

        btnSave.setOnClickListener(v -> {
            try {
                int workMinutes = Integer.parseInt(etWork.getText().toString());
                int shortBreakMinutes = Integer.parseInt(etShortBreak.getText().toString());
                int longBreakMinutes = Integer.parseInt(etLongBreak.getText().toString());
                int loopCount = Integer.parseInt(etLoopCount.getText().toString());

                // Validasi input
                if (workMinutes < 1 || shortBreakMinutes < 1 || longBreakMinutes < 1 || loopCount < 1) {
                    throw new NumberFormatException("Value must be at least 1");
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("work_time", workMinutes * 60 * 1000);
                editor.putLong("short_break_time", shortBreakMinutes * 60 * 1000);
                editor.putLong("long_break_time", longBreakMinutes * 60 * 1000);
                editor.putInt("loop_count", loopCount);
                editor.apply();

                finish();
            } catch (NumberFormatException e) {
                // Handle invalid input
                if (etWork.getText().toString().isEmpty()) etWork.setError("Required");
                if (etShortBreak.getText().toString().isEmpty()) etShortBreak.setError("Required");
                if (etLongBreak.getText().toString().isEmpty()) etLongBreak.setError("Required");
                if (etLoopCount.getText().toString().isEmpty()) etLoopCount.setError("Required");
            }
        });
    }
}