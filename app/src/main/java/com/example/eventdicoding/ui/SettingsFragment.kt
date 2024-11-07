package com.example.eventdicoding.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.datastore.SettingsPreferences
import com.example.eventdicoding.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val settingsPreferences by lazy { SettingsPreferences(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeDarkModeSwitch()
    }

    private fun initializeDarkModeSwitch() {
        observeDarkModeSetting()
        setUpDarkModeSwitchListener()
    }

    private fun observeDarkModeSetting() {
        lifecycleScope.launch {
            settingsPreferences.darkMode.collect { isDarkModeEnabled ->
                binding.switchDarkMode.isChecked = isDarkModeEnabled
            }
        }
    }

    private fun setUpDarkModeSwitchListener() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            updateDarkModePreference(isChecked)
        }
    }

    private fun updateDarkModePreference(isEnabled: Boolean) {
        lifecycleScope.launch {
            settingsPreferences.updateDarkMode(isEnabled)
            AppCompatDelegate.setDefaultNightMode(
                if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}
