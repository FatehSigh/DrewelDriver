package com.octalsoftware.drewel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.HomeActivity;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.utils.Prefs;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        try {
            if (AppDelegate.Companion.isValidString(new Prefs(this).getDefaultLanguage())) {
            } else {
                new Prefs(this).setDefaultLanguage(Tags.LANGUAGE_ENGLISH);
            }
        } catch (Exception e) {
            new Prefs(this).setDefaultLanguage(Tags.LANGUAGE_ENGLISH);
        }
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (new Prefs(SplashActivity.this).getUserdata() == null) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 3000);
    }
}
