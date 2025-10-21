package com.example.stayfocus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private TextView tvSession, tvTimer, tvActivityName, tvLoopInfo;
    private Button btnPlayPause, btnStop;
    private LinearLayout mainLayout;

    private CountDownTimer timer;
    private boolean isRunning = false;
    private boolean isPaused = false;

    // Variabel untuk sistem loop
    private int loopCount = 1;
    private int currentLoop = 1;
    private int workSessionsCompleted = 0;

    private long timeLeftInMillis;
    private long workTime;
    private long shortBreakTime;
    private long longBreakTime;

    private MediaPlayer mediaPlayer;
    private MediaPlayer backgroundMusicPlayer;
    private Vibrator vibrator;
    private SharedPreferences prefs;
    private SharedPreferences soundPrefs;
    private String activityName = "";

    private boolean soundEnabled = true;
    private boolean backgroundMusicEnabled = false;
    private int selectedMusicResource = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initializeViews();
        loadSettings();
        setupHeaderNavigation();
        setupTimer();

        btnPlayPause.setOnClickListener(v -> toggleTimer());
        btnStop.setOnClickListener(v -> stopTimer());
    }

    private void initializeViews() {
        mainLayout = findViewById(R.id.mainLayout);
        tvSession = findViewById(R.id.tvSession);
        tvTimer = findViewById(R.id.tvTimer);
        tvActivityName = findViewById(R.id.tvActivityName);
        tvLoopInfo = findViewById(R.id.tvLoopInfo);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        prefs = getSharedPreferences("PomodoroHistory", MODE_PRIVATE);
        soundPrefs = getSharedPreferences("SoundSettings", MODE_PRIVATE);
    }

    private void loadSettings() {
        // Load timer settings from intent
        Intent intent = getIntent();
        workTime = intent.getLongExtra("work_time", 25 * 60 * 1000);
        shortBreakTime = intent.getLongExtra("short_break_time", 5 * 60 * 1000);
        longBreakTime = intent.getLongExtra("long_break_time", 15 * 60 * 1000);

        // Load loop settings
        loopCount = prefs.getInt("loop_count", 1);

        // Load activity name
        activityName = intent.getStringExtra("activity_name");
        if (activityName != null) {
            tvActivityName.setText(activityName);
        }

        // Load sound settings
        loadSoundSettings();

        // Update loop info display
        updateLoopInfoDisplay();
    }

    private void loadSoundSettings() {
        soundEnabled = soundPrefs.getBoolean("sound_enabled", true);
        backgroundMusicEnabled = soundPrefs.getBoolean("background_music", false);
        int selectedMusicPosition = soundPrefs.getInt("selected_music", 0);

        // Map spinner position ke resource ID
        switch (selectedMusicPosition) {
            case 1:
                selectedMusicResource = R.raw.music_modern;
                break;
            case 2:
                selectedMusicResource = R.raw.lofi_music;
                break;
            case 3:
                selectedMusicResource = R.raw.phonk_music;
                break;
            default:
                selectedMusicResource = 0;
        }
    }

    private void updateLoopInfoDisplay() {
        if (tvLoopInfo != null) {
            String loopText = "Loop: " + currentLoop + "/" + loopCount +
                    " | Session: " + (workSessionsCompleted % 4 + 1) + "/4";
            tvLoopInfo.setText(loopText);
        }
    }

    private void setupHeaderNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(TimerActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(TimerActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(TimerActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    private void setupTimer() {
        startWorkSession();
    }

    private void startWorkSession() {
        isPaused = false;
        tvSession.setText("Work");
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

        if (soundEnabled) {
            playSound(R.raw.work);
        }

        // Start background music jika diaktifkan
        if (backgroundMusicEnabled && selectedMusicResource != 0) {
            startBackgroundMusic();
        }

        timeLeftInMillis = workTime;
        updateTimerText();
        btnPlayPause.setText("❚❚");

        // Auto-start timer untuk work session pertama
        if (!isRunning && !isPaused) {
            startTimer();
        }

        updateLoopInfoDisplay();
    }

    private void startShortBreak() {
        isPaused = false;
        tvSession.setText("Short Break");
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));

        if (soundEnabled) {
            playSound(R.raw.break_short);
        }

        timeLeftInMillis = shortBreakTime;
        updateTimerText();

        // Simpan history untuk work session yang selesai
        saveHistory("Work Session " + workSessionsCompleted + " Completed");

        btnPlayPause.setText("❚❚");
        updateLoopInfoDisplay();
        startTimer();
    }

    private void startLongBreak() {
        isPaused = false;
        tvSession.setText("Long Break");
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        if (soundEnabled) {
            playSound(R.raw.break_long);
        }

        timeLeftInMillis = longBreakTime;
        updateTimerText();

        // Simpan history untuk loop yang selesai
        saveHistory("Loop " + (currentLoop - 1) + " Completed - " + workSessionsCompleted + " Work Sessions");

        btnPlayPause.setText("❚❚");
        updateLoopInfoDisplay();
        startTimer();
    }

    private void startBackgroundMusic() {
        stopBackgroundMusic();
        if (selectedMusicResource != 0) {
            try {
                backgroundMusicPlayer = MediaPlayer.create(this, selectedMusicResource);
                if (backgroundMusicPlayer != null) {
                    backgroundMusicPlayer.setLooping(true);
                    backgroundMusicPlayer.setVolume(0.3f, 0.3f); // Volume rendah
                    backgroundMusicPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            if (backgroundMusicPlayer.isPlaying()) {
                backgroundMusicPlayer.stop();
            }
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
    }

    private void toggleTimer() {
        if (isRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (isPaused) {
            // Resume from paused state
            createTimer(timeLeftInMillis);
        } else {
            // Start new session dengan waktu sesuai session type
            createTimer(timeLeftInMillis);
        }

        isRunning = true;
        isPaused = false;
        btnPlayPause.setText("❚❚");
    }

    private void createTimer(long duration) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;

                if (soundEnabled) {
                    playCompletionSound();
                }
                vibrate();

                // Logika session transition dengan sistem loop
                String currentSession = tvSession.getText().toString();

                if (currentSession.equals("Work")) {
                    workSessionsCompleted++;

                    // Check if completed 4 work sessions (1 loop)
                    if (workSessionsCompleted % 4 == 0) {
                        // Check if all loops completed
                        if (currentLoop >= loopCount) {
                            // Semua loop selesai
                            showCompletionMessage();
                            return;
                        } else {
                            // Masih ada loop berikutnya
                            currentLoop++;
                            startLongBreak();
                        }
                    } else {
                        // Masih dalam loop yang sama, lanjut short break
                        startShortBreak();
                    }
                } else {
                    // Break selesai, lanjut work session
                    startWorkSession();
                }
            }
        }.start();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        isPaused = true;
        btnPlayPause.setText("▶");

        // Pause background music juga
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.pause();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }

        // Stop semua musik
        stopBackgroundMusic();

        // Save current session progress jika hampir selesai
        String currentSession = tvSession.getText().toString();
        if (currentSession.equals("Work") && timeLeftInMillis < (workTime * 0.1)) {
            saveHistory("Work Session " + (workSessionsCompleted + 1) + " (Stopped)");
        }

        // Kembali ke main activity
        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showCompletionMessage() {
        // Stop semua timer
        if (timer != null) {
            timer.cancel();
        }

        // Stop background music
        stopBackgroundMusic();

        // Tampilkan pesan penyelesaian
        tvSession.setText("Completed!");
        tvTimer.setText("🎉 Done!");
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        if (tvLoopInfo != null) {
            tvLoopInfo.setText("All " + loopCount + " loops completed!");
        }

        // Save final history untuk semua loop selesai
        saveHistory("All " + loopCount + " Loops Completed - Total: " +
                workSessionsCompleted + " Work Sessions");

        // Tampilkan toast
        Toast.makeText(this, "Congratulations! All " + loopCount + " loops completed!",
                Toast.LENGTH_LONG).show();

        // Nonaktifkan tombol
        btnPlayPause.setEnabled(false);
        btnStop.setEnabled(false);

        // Kembali ke MainActivity setelah 5 detik
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(TimerActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 5000);
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(formatted);
    }

    private void playSound(int soundResId) {
        if (!soundEnabled) return;

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playCompletionSound() {
        if (!soundEnabled) return;

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.work);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 500, 200, 500}; // Vibrate pattern
            vibrator.vibrate(pattern, -1);
        }
    }

    private void saveHistory(String sessionType) {
        String current = prefs.getString("history", "");
        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Tentukan info loop
        String loopInfo = "Loop " + currentLoop + "/" + loopCount;

        String record = activityName + "|" + sessionType + "|" + date + "|" + time + "|" + loopInfo + "\n";

        // Get current total count
        int total = prefs.getInt("total_sessions", 0);
        total++;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("history", current + record);
        editor.putInt("total_sessions", total);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null && isRunning) {
            pauseTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload sound settings setiap kali resume
        loadSoundSettings();
        updateLoopInfoDisplay();
    }
}