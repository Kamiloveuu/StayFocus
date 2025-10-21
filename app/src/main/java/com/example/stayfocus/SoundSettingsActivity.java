
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

    // Daftar lagu yang tersedia
    private final String[] musicList = {
            "Pilih Musik",
            "Music Modern",
            "Musik Lofi",
            "Music Phonk",
    };

    // Resource ID untuk setiap lagu (simpan file musik di res/raw)
    private final int[] musicResources = {
            0, // 0 untuk "Pilih Musik"
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
        setupSaveButton();
        setupMusicPreview();
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

    private void setupMusicPreview() {
        spinnerMusic.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                // Stop musik yang sedang diputar
                stopMusicPreview();

                // Jika bukan pilihan default dan background music diaktifkan, putar preview
                if (position > 0 && cbBackgroundMusic.isChecked()) {
                    playMusicPreview(position);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                stopMusicPreview();
            }
        });

        // Juga stop preview ketika background music dinonaktifkan
        cbBackgroundMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                stopMusicPreview();
            }
        });
    }

    private void playMusicPreview(int position) {
        if (position > 0 && position < musicResources.length) {
            try {
                stopMusicPreview();
                mediaPlayer = MediaPlayer.create(this, musicResources[position]);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memutar preview musik", Toast.LENGTH_SHORT).show();
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

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            int selectedMusicPosition = spinnerMusic.getSelectedItemPosition();

            // Validasi: jika background music diaktifkan, harus pilih musik
            if (cbBackgroundMusic.isChecked() && selectedMusicPosition == 0) {
                Toast.makeText(this, "Silakan pilih musik terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("sound_enabled", cbSound.isChecked());
            editor.putBoolean("background_music", cbBackgroundMusic.isChecked());
            editor.putInt("selected_music", selectedMusicPosition);

            // Simpan nama musik yang dipilih
            if (selectedMusicPosition > 0 && selectedMusicPosition < musicList.length) {
                editor.putString("selected_music_name", musicList[selectedMusicPosition]);
            } else {
                editor.putString("selected_music_name", "");
            }

            editor.apply();

            Toast.makeText(this, "Pengaturan suara disimpan", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusicPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusicPreview();
    }
}

