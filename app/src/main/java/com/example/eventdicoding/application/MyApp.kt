package com.example.eventdicoding.application

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.eventdicoding.util.workmanager.WorkFactory

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeWorkManager()
    }

    private fun initializeWorkManager() {
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(WorkFactory())
                .build()
        )
    }
}