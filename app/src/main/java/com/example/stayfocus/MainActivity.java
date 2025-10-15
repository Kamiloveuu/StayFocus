package com.example.stayfocus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private EditText etActivityName;
    private Button btnStart, btnReport, btnSettings, btnInfo;
    private TextView tvWelcome;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etActivityName = findViewById(R.id.etActivityName);
        btnStart = findViewById(R.id.btnStart);
        btnReport = findViewById(R.id.btnReport);
        btnSettings = findViewById(R.id.btnSettings);
        btnInfo = findViewById(R.id.btnInfo);


        prefs = getSharedPreferences("StayFocusHistory", MODE_PRIVATE);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);

        btnStart.setOnClickListener(v -> {
            String activityName = etActivityName.getText().toString().trim();
            if (activityName.isEmpty()) {
                etActivityName.setError("Enter activity name");
                return;
            }

            // Simpan nama terakhir untuk ditampilkan di welcome
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_activity", activityName);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            intent.putExtra("activity_name", activityName);
            startActivity(intent);
        });

        showLastActivity();
    }

    private void showLastActivity() {
        String last = prefs.getString("last_activity", "");
        if (!last.isEmpty()) {
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
            tvWelcome.setText("Last: " + last + " • " + date);
        } else {
            tvWelcome.setText("Focus starts here");
        }
    }
}
