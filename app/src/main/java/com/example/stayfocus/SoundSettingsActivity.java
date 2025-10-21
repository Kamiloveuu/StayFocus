package com.example.stayfocus;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SoundSettingsActivity extends AppCompatActivity {

    private CheckBox cbSound, cbBackgroundMusic;
    private Spinner spinnerMusic;
    private Button btnSave;
    private SharedPreferences prefs;
    private MediaPlayer mediaPlayer;

    private final String[] musicList = {
            "Pilih Musik",
            "Music Modern",
            "Musik Lofi",
            "Music Phonk",
    };

    private final int[] musicResources = {
            0,
            R.raw.music_modern,
            R.raw.lofi_music,
            R.raw.phonk_music,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_sound);

        initializeViews();
        setupNavigation();
        setupMusicSpinner();
        loadSettings();
        setupAutoSaveListeners();
    }

    private void initializeViews() {
        cbSound = findViewById(R.id.cbSound);
        cbBackgroundMusic = findViewById(R.id.cbBackgroundMusic);
        spinnerMusic = findViewById(R.id.spinnerMusic);
        btnSave = findViewById(R.id.btnSave);

        prefs = getSharedPreferences("SoundSettings", MODE_PRIVATE);
    }

    private void setupNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void setupMusicSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                musicList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMusic.setAdapter(adapter);
    }

    private void loadSettings() {
        boolean soundEnabled = prefs.getBoolean("sound_enabled", true);
        boolean backgroundMusic = prefs.getBoolean("background_music", false);
        int selectedMusic = prefs.getInt("selected_music", 0);

        cbSound.setChecked(soundEnabled);
        cbBackgroundMusic.setChecked(backgroundMusic);

        if (selectedMusic >= 0 && selectedMusic < musicList.length) {
            spinnerMusic.setSelection(selectedMusic);
        }
    }

    private void setupAutoSaveListeners() {
        // Auto save ketika sound enabled diubah
        cbSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSoundSettings();
            if (!isChecked) {
                // Jika sound dimatikan, matikan juga background music
                cbBackgroundMusic.setChecked(false);
                stopMusicPreview();
            }
        });

        // Auto save ketika background music diubah
        cbBackgroundMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSoundSettings();
            if (isChecked) {
                // Jika background music diaktifkan, putar preview
                int pos = spinnerMusic.getSelectedItemPosition();
                if (pos > 0) {
                    playMusicPreview(pos);
                }
            } else {
                stopMusicPreview();
            }
        });

        // Auto save + preview ketika musik dipilih
        spinnerMusic.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                saveSoundSettings();

                stopMusicPreview();
                if (cbBackgroundMusic.isChecked() && position > 0) {
                    playMusicPreview(position);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                stopMusicPreview();
            }
        });

        // Tombol save untuk konfirmasi
        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Pengaturan suara tersimpan", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveSoundSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sound_enabled", cbSound.isChecked());
        editor.putBoolean("background_music", cbBackgroundMusic.isChecked());
        editor.putInt("selected_music", spinnerMusic.getSelectedItemPosition());
        editor.putString("selected_music_name", musicList[spinnerMusic.getSelectedItemPosition()]);
        editor.apply();
    }

    private void playMusicPreview(int position) {
        if (position > 0 && position < musicResources.length) {
            stopMusicPreview();
            try {
                mediaPlayer = MediaPlayer.create(this, musicResources[position]);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memutar preview", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopMusicPreview() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusicPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusicPreview();
    }
}