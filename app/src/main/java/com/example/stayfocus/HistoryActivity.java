package com.example.stayfocus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private TextView tvTotal, tvStatus, tvEmptyState;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private SharedPreferences prefs;
    private Button btnClearHistory;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();
        setupNavigation();
        loadHistory();
        setupHomeButton();
    }

    private void initializeViews() {
        tvTotal = findViewById(R.id.tvTotal);
        tvStatus = findViewById(R.id.tvStatus);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView = findViewById(R.id.recyclerView);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        prefs = getSharedPreferences("PomodoroHistory", MODE_PRIVATE);

        // Setup RecyclerView
        adapter = new HistoryAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup clear history button
        btnClearHistory.setOnClickListener(v -> clearHistory());
    }

    private void setupNavigation() {
        Button btnReport = findViewById(R.id.btnReport);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnInfo = findViewById(R.id.btnInfo);

        NavigationHelper.setupAllHeaderNavigation(this, btnReport, btnSettings, btnInfo);
    }

    private void loadHistory() {
        int totalSessions = prefs.getInt("total_sessions", 0);
        tvTotal.setText(String.valueOf(totalSessions));

        // Set status based on total sessions
        setStatusLevel(totalSessions);

        // Load history records
        String history = prefs.getString("history", "");
        List<HistoryRecord> records = parseHistory(history);

        // Update adapter
        adapter.updateData(records);

        // Tampilkan empty state jika tidak ada history
        if (records.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnClearHistory.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btnClearHistory.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusLevel(int totalSessions) {
        String status;
        int statusColor;

        if (totalSessions == 0) {
            status = "BEGINNER";
            statusColor = ContextCompat.getColor(this, R.color.status_beginner);
        } else if (totalSessions < 10) {
            status = "BEGINNER";
            statusColor = ContextCompat.getColor(this, R.color.status_beginner);
        } else if (totalSessions < 20) {
            status = "ACTIVE";
            statusColor = ContextCompat.getColor(this, R.color.status_active);
        } else if (totalSessions < 30) {
            status = "CONSISTENT";
            statusColor = ContextCompat.getColor(this, R.color.status_consistent);
        } else {
            status = "FOCUS MASTER";
            statusColor = ContextCompat.getColor(this, R.color.status_focus);
        }

        tvStatus.setText(status);
        tvStatus.setTextColor(statusColor);
    }

    private List<HistoryRecord> parseHistory(String history) {
        List<HistoryRecord> records = new ArrayList<>();
        if (history.isEmpty()) return records;

        String[] entries = history.split("\n");
        for (String entry : entries) {
            String[] parts = entry.split("\\|");
            if (parts.length >= 4) {
                String activityName = parts[0];
                String sessionType = parts[1];
                String date = parts[2];
                String time = parts[3];
                String loopInfo = parts.length >= 5 ? parts[4] : "";

                records.add(0, new HistoryRecord(activityName, sessionType, date, time, loopInfo));
            }
        }
        return records;
    }

    private void clearHistory() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("history");
        editor.remove("total_sessions");
        editor.apply();

        // Update UI
        tvTotal.setText("0");
        setStatusLevel(0);
        adapter.clearData();


        tvEmptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        btnClearHistory.setVisibility(View.GONE);


        android.widget.Toast.makeText(this, "History cleared successfully",
                android.widget.Toast.LENGTH_SHORT).show();
    }

    private void setupHomeButton() {
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }
}
