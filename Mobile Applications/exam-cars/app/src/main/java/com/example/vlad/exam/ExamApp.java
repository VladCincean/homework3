package com.example.vlad.exam;

import android.app.Application;
import android.util.Log;

import com.example.vlad.exam.room.AppDatabase;

import timber.log.Timber;

/**
 * Created by vlad on 29.01.2018.
 */

public class ExamApp extends Application {
    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Timber.d("Timber - onCreate");
        Log.d("EXAM-APP", "Logger - onCreate");

        db = AppDatabase.getInstance(this);
    }
}
