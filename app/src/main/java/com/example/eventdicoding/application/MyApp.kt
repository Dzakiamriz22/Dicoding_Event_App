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
        val workerFactory = WorkFactory()
        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, config)
    }
}
