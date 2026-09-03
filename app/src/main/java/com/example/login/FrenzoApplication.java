package com.example.login;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class FrenzoApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        Map<String, Object> config =
                new HashMap<>();

        config.put(
                "cloud_name",
                "klhzpjaj"
        );

        config.put(
                "secure",
                true
        );

        MediaManager.init(
                this,
                config
        );
    }
}