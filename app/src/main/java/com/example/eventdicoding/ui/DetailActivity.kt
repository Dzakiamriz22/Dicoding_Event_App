package com.example.eventdicoding.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.local.datastore.SettingsPreferences
import com.example.eventdicoding.data.local.entity.Event
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.ActivityDetailBinding
import com.example.eventdicoding.util.DateTime.convertDate
import com.example.eventdicoding.util.EventUtil
import com.example.eventdicoding.viewmodel.DetailViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(R.layout.activity_detail) {
    private val binding by viewBinding(ActivityDetailBinding::bind)
    private val detailViewModel by lazy {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(this).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        ViewModelProvider(this, factory)[DetailViewModel::class.java]
    }
    private val settingsPreference by lazy { SettingsPreferences(this) }
    private var url = ""
    private var isFavorite = false
    private val eventId: Int by lazy { intent.getIntExtra("event_id", EventUtil.eventId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
        fetchEventDetails()
        checkFavoriteStatus()
    }

    private fun setupUI() {
        binding.apply {
            backButton.setOnClickListener { finish() }
            webButton.setOnClickListener { openWebPage(url) }
            favorite.setOnClickListener { toggleFavoriteStatus() }
        }
    }

    private fun setupObservers() {
        detailViewModel.apply {
            event.observe(this@DetailActivity) { event -> displayEventDetails(event) }
            exception.observe(this@DetailActivity) { showError(it) }
            isLoading.observe(this@DetailActivity) { toggleLoading(it) }
            isEventExistInFavorite.observe(this@DetailActivity) { updateFavoriteIcon(it) }
        }

        lifecycleScope.launch {
            settingsPreference.darkMode.collect { isEnabled ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    private fun fetchEventDetails() {
        detailViewModel.getEvent(eventId)
    }

    private fun checkFavoriteStatus() {
        detailViewModel.checkIsEventExistInFavorite(eventId)
    }

    private fun displayEventDetails(event: com.example.eventdicoding.data.remote.model.Event) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(event.imageLogo)
                .centerCrop()
                .into(ivEventImage)
            url = event.link
            eventTitle.text = event.name
            eventOrganizer.text = event.ownerName
            eventCategory.text = event.category
            eventTime.text = convertDate(this@DetailActivity, event.beginTime, event.endTime)
            eventLocation.text = event.cityName // Ensure cityName exists in Event
            eventQuota.text = (event.quota - event.registrants).toString() // Ensure quota and registrants exist in Event
            eventDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_LEGACY) // Ensure description exists
            svDetail.visibility = View.VISIBLE
            cvWebButton.visibility = View.VISIBLE
            failedLoadData.visibility = View.GONE
        }
        EventUtil.eventDetail = event
    }

    private fun showError(hasError: Boolean) {
        if (hasError) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
            binding.failedLoadData.visibility = View.VISIBLE
            detailViewModel.resetExceptionValue()
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.apply {
            svDetail.visibility = if (isLoading) View.GONE else View.VISIBLE
            cvWebButton.visibility = if (isLoading) View.GONE else View.VISIBLE
            progBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
    }

    private fun toggleFavoriteStatus() {
        if (!isFavorite) {
            saveEventToFavorite()
        } else {
            removeEventFromFavorite()
        }
        isFavorite = !isFavorite
    }

    private fun saveEventToFavorite() {
        EventUtil.eventDetail?.let { event ->
            detailViewModel.saveEventToFavorite(
                Event(event.id, event.name, event.imageLogo, event.category, event.beginTime, event.endTime) // Only include existing properties
            )
            Toast.makeText(this, R.string.success_save_to_favorite, Toast.LENGTH_SHORT).show()
            updateFavoriteIcon(true)
        }
    }

    private fun removeEventFromFavorite() {
        detailViewModel.removeEventFromFavorite(eventId)
        Toast.makeText(this, R.string.success_remove_from_favorite, Toast.LENGTH_SHORT).show()
        updateFavoriteIcon(false)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.favorite.setImageResource(
            if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )
    }
}
