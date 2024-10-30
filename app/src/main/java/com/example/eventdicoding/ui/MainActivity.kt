package com.example.eventdicoding.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.datastore.SettingsPreferences
import com.example.eventdicoding.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)

    private val settingsPreference by lazy {
        SettingsPreferences(this)
    }

    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
        setupPreferenceObservers()
    }

    private fun initializeUI() {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.navView.setupWithNavController(navController)
    }

    private fun setupPreferenceObservers() {
        lifecycleScope.launch {
            settingsPreference.darkMode.collect { isEnabled ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }
}