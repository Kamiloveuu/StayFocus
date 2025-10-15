package com.example.stayfocus;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationHelper {

    /**
     * Universal function untuk setup header button navigation
     * @param currentActivity Activity saat ini
     * @param button Button yang akan di-setup
     * @param targetActivity Activity tujuan
     */
    public static void setupHeaderButtonNavigation(AppCompatActivity currentActivity, Button button, Class<?> targetActivity) {
        if (button != null) {
            button.setOnClickListener(v -> {
                Intent intent = new Intent(currentActivity, targetActivity);
                currentActivity.startActivity(intent);
            });
        }
    }

    /**
     * Setup semua header buttons sekaligus
     * @param currentActivity Activity saat ini
     * @param btnReport Button Report
     * @param btnSettings Button Settings
     * @param btnInfo Button Info
     */
    public static void setupAllHeaderNavigation(AppCompatActivity currentActivity,
                                                Button btnReport,
                                                Button btnSettings,
                                                Button btnInfo) {
        setupHeaderButtonNavigation(currentActivity, btnReport, HistoryActivity.class);
        setupHeaderButtonNavigation(currentActivity, btnSettings, SettingsActivity.class);
        setupHeaderButtonNavigation(currentActivity, btnInfo, InfoActivity.class);
    }
}