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

    private val settingsPreference by lazy {
        SettingsPreferences(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwitchButton()
    }

    private fun setupSwitchButton() {
        // Observe the dark mode state to set the initial state of the switch
        lifecycleScope.launch {
            settingsPreference.darkMode.collect { isEnabled ->
                binding.darkModeSwitch.isChecked = isEnabled
            }
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                // Update the DataStore when the switch is toggled
                settingsPreference.updateDarkMode(isChecked)

                // Apply dark mode immediately
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }
}
