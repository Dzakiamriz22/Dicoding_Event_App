package com.example.dicodingevent.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DataInstance {
    private const val DATA_STORE_NAME = "settings_preference"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE_NAME)

    fun getInstance(context: Context): DataStore<Preferences> = context.dataStore
}
