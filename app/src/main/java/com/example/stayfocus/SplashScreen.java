package com.example.stayfocus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private ImageView imgLogo;
    private TextView tvLoading;


    @Override
    protected void onPause() {
        super.onPause();
        imgLogo.clearAnimation();
        if (tvLoading != null) {
            tvLoading.clearAnimation();
        }
    }
}