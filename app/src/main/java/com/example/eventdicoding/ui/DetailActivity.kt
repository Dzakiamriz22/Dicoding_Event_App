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
    private val settingsPreference by lazy {
        SettingsPreferences(this)  // Langsung gunakan context `this`
    }
    private var url = ""
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupButton()
        getEvent()
        checkIsEventExistInFavorite()
        setupObservers()
    }

    private fun setupButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.webButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        binding.favorite.setOnClickListener {
            if (!isFavorite) {
                binding.favorite.setImageResource(R.drawable.baseline_favorite_24)
                saveEventToFavorite()
            } else {
                binding.favorite.setImageResource(R.drawable.baseline_favorite_border_24)
                removeEventFromFavorite(EventUtil.eventId)
            }
            isFavorite = !isFavorite
        }
    }

    private fun getEvent() {
        if (EventUtil.eventId == 0) {
            detailViewModel.getEvent(intent.getIntExtra("event_id", 0))
        } else {
            detailViewModel.getEvent(EventUtil.eventId)
        }
    }

    private fun checkIsEventExistInFavorite() {
        detailViewModel.checkIsEventExistInFavorite(EventUtil.eventId)
    }

    private fun setupObservers() {
        detailViewModel.event.observe(this) {
            Glide.with(this)
                .load(it.imageLogo)
                .centerCrop()
                .into(binding.ivEventImage)
            url = it.link
            val htmlText = it.description

            binding.apply {
                eventTitle.text = it.name
                eventOrganizer.text = it.ownerName
                eventCategory.text = it.category
                eventTime.text = convertDate(this@DetailActivity, it.beginTime, it.endTime)
                eventLocation.text = it.cityName
                eventQuota.text = (it.quota - it.registrants).toString()
                eventDescription.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
                svDetail.visibility = View.VISIBLE
                cvWebButton.visibility = View.VISIBLE
                failedLoadData.visibility = View.GONE
            }
            EventUtil.eventDetail = it
        }

        detailViewModel.exception.observe(this) {
            if (it) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
                binding.failedLoadData.visibility = View.VISIBLE
                detailViewModel.resetExceptionValue()
            }
        }

        detailViewModel.isLoading.observe(this) {
            if (it) {
                binding.svDetail.visibility = View.GONE
                binding.cvWebButton.visibility = View.GONE
                binding.progBar.visibility = View.VISIBLE
            } else {
                binding.progBar.visibility = View.GONE
            }
        }

        detailViewModel.isEventExistInFavorite.observe(this) {
            isFavorite = it
            if (isFavorite) {
                binding.favorite.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.favorite.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }

        lifecycleScope.launch {
            settingsPreference.darkMode.collect { isEnabled ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    private fun saveEventToFavorite() {
        EventUtil.eventDetail?.let {
            detailViewModel.saveEventToFavorite(
                Event(
                    it.id,
                    it.name,
                    it.imageLogo,
                    it.category,
                    it.beginTime,
                    it.endTime
                )
            )
        }
        Toast.makeText(
            this,
            resources.getString(R.string.success_save_to_favorite),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun removeEventFromFavorite(id: Int) {
        detailViewModel.removeEventFromFavorite(id)
        Toast.makeText(
            this,
            resources.getString(R.string.success_remove_from_favorite),
            Toast.LENGTH_SHORT
        ).show()
    }
}
