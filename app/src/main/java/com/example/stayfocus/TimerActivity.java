package com.example.stayfocus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private TextView tvSession, tvTimer, tvActivityName;
    private Button btnPlayPause, btnStop, btnReport, btnSettings, btnInfo;
    private LinearLayout mainLayout;

    private CountDownTimer timer;
    private boolean isRunning = false;
    private boolean isWorkSession = true;
    private int sessionCount = 0;

    private long timeLeftInMillis;
    private final long WORK_TIME = 25 * 60 * 1000;
    private final long SHORT_BREAK = 5 * 60 * 1000;
    private final long LONG_BREAK = 15 * 60 * 1000;

    private MediaPlayer mediaPlayer;
    private SharedPreferences prefs;
    private String activityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initializeViews();
        setupHeaderNavigation();

        prefs = getSharedPreferences("PomodoroHistory", MODE_PRIVATE);

        // Ambil nama activity dari MainActivity
        activityName = getIntent().getStringExtra("activity_name");
        if (activityName != null) tvActivityName.setText(activityName);

        startWorkSession();

        btnPlayPause.setOnClickListener(v -> {
            if (isRunning) pauseTimer();
            else startTimer();
        });

        btnStop.setOnClickListener(v -> resetAll());
    }

    private void initializeViews() {
        mainLayout = findViewById(R.id.mainLayout);
        tvSession = findViewById(R.id.tvSession);
        tvTimer = findViewById(R.id.tvTimer);
        tvActivityName = findViewById(R.id.tvActivityName);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);
        btnReport = findViewById(R.id.btnReport);
        btnSettings = findViewById(R.id.btnSettings);
        btnInfo = findViewById(R.id.btnInfo);
    }

    private void setupHeaderNavigation() {
        // Pastikan NavigationHelper sudah ada
        // Jika belum, sementara bisa dikosongkan
        // NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void startWorkSession() {
        isWorkSession = true;
        tvSession.setText("Work");
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        playSound(R.raw.work);
        timeLeftInMillis = WORK_TIME;
        updateTimerText();
    }

    private void startBreakSession() {
        isWorkSession = false;
        sessionCount++;

        if (sessionCount % 4 == 0) {
            tvSession.setText("Long Break");
            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            playSound(R.raw.break_long);
            timeLeftInMillis = LONG_BREAK;
            saveHistory("Long Break");
        } else {
            tvSession.setText("Short Break");
            mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            playSound(R.raw.break_short);
            timeLeftInMillis = SHORT_BREAK;
            saveHistory("Short Break");
        }
        updateTimerText();
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                if (isWorkSession) {
                    saveHistory("Work");
                    startBreakSession();
                } else {
                    startWorkSession();
                }
                startTimer();
            }
        }.start();

        isRunning = true;
        btnPlayPause.setText("❚❚");
    }

    private void pauseTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
        btnPlayPause.setText("▶");
    }

    private void resetAll() {
        if (timer != null) timer.cancel();
        isRunning = false;
        sessionCount = 0;
        startWorkSession();
        btnPlayPause.setText("▶");
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(formatted);
    }

    private void playSound(int soundResId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResId);
        mediaPlayer.start();
    }

    private void saveHistory(String sessionType) {
        String current = prefs.getString("history", "");
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String record = activityName + " (" + sessionType + ")" + "|" + date + "|" + time + ";\n";
        prefs.edit().putString("history", current + record).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        if (mediaPlayer != null) mediaPlayer.release();
    }
}
