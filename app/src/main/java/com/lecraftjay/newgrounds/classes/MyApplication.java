package com.lecraftjay.newgrounds.classes;

import android.app.Application;
import android.os.Build;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MyApplication extends Application {

    private static AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpenManager = new AppOpenManager(this);
        }

    }
}