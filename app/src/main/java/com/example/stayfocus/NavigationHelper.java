package com.example.stayfocus;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationHelper {

    public static void setupHeaderButtonNavigation(AppCompatActivity currentActivity,
                                                   Button button, Class<?> targetActivity) {
        if (button != null) {
            button.setOnClickListener(v -> {
                // Cegah membuka activity yang sama
                if (currentActivity.getClass() != targetActivity) {
                    Intent intent = new Intent(currentActivity, targetActivity);
                    currentActivity.startActivity(intent);
                }
            });
        }
    }

    public static void setupAllHeaderNavigation(AppCompatActivity currentActivity,
                                                Button btnReport,
                                                Button btnSettings,
                                                Button btnInfo) {
        if (btnReport != null) {
            btnReport.setOnClickListener(v -> {
                // Hanya navigasi jika bukan activity yang sama
                if (currentActivity.getClass() != HistoryActivity.class) {
                    Intent intent = new Intent(currentActivity, HistoryActivity.class);
                    currentActivity.startActivity(intent);
                }
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                // Hanya navigasi jika bukan activity yang sama
                if (currentActivity.getClass() != SettingsActivity.class) {
                    Intent intent = new Intent(currentActivity, SettingsActivity.class);
                    currentActivity.startActivity(intent);
                }
            });
        }

        if (btnInfo != null) {
            btnInfo.setOnClickListener(v -> {
                // Hanya navigasi jika bukan activity yang sama
                if (currentActivity.getClass() != InfoActivity.class) {
                    Intent intent = new Intent(currentActivity, InfoActivity.class);
                    currentActivity.startActivity(intent);
                }
            });
        }
    }
}