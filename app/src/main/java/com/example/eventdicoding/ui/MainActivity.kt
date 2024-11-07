package com.example.eventdicoding.ui

import android.content.Intent
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
    private val settingsPreference by lazy { SettingsPreferences(this) }
    private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment_activity_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
        observeDarkModeSetting()
    }

    private fun initializeUI() {
        setupBottomNavigation()
        setupSearchButton()
    }

    private fun setupBottomNavigation() {
        binding.navView.setupWithNavController(navController)
    }

    private fun setupSearchButton() {
        binding.buttonSearch.setOnClickListener {
            startActivity(Intent(this, EventSearchActivity::class.java))
        }
    }

    private fun observeDarkModeSetting() {
        lifecycleScope.launch {
            settingsPreference.darkMode.collect { isDarkModeEnabled ->
                applyDarkMode(isDarkModeEnabled)
            }
        }
    }

    private fun applyDarkMode(isDarkModeEnabled: Boolean) {
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
