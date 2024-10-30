package com.example.eventdicoding.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.eventdicoding.R
import com.example.eventdicoding.adapters.SearchAdapter
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.ActivitySearchBinding
import com.example.eventdicoding.viewmodel.SearchViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory

class SearchActivity : AppCompatActivity(R.layout.activity_search) {
    private val binding by viewBinding(ActivitySearchBinding::bind)
    private val searchViewModel by lazy {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(this).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        setupButton()
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupButton() {
        binding.backButton.setOnClickListener { finish() }
        binding.progBar.visibility = View.GONE
        binding.noResult.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(emptyList())
        binding.rvUpcomingEvent.layoutManager = LinearLayoutManager(this)
        binding.rvUpcomingEvent.adapter = searchAdapter
    }

    private fun setupSearchView() {
        binding.searchView.queryHint = "Cari Event"
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.textSize = 14f
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.charcoal_grey))

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        searchViewModel.getEventsByKeyword(it.trim())
                        binding.progBar.visibility = View.VISIBLE // Show progress bar
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Jika Anda ingin melakukan pencarian saat mengetik, bisa tambahkan logika di sini
                return false
            }
        })
    }

    private fun setupObservers() {
        searchViewModel.eventsByKeyword.observe(this) { events ->
            binding.progBar.visibility = View.GONE // Hide progress bar after loading
            if (events != null && events.isNotEmpty()) {
                searchAdapter.updateEvents(events)
                binding.rvUpcomingEvent.visibility = View.VISIBLE
                binding.noResult.visibility = View.GONE
            } else {
                binding.rvUpcomingEvent.visibility = View.GONE
                binding.noResult.text = getString(R.string.no_result)
                binding.noResult.visibility = View.VISIBLE
            }
        }

        searchViewModel.exception.observe(this) { hasException ->
            if (hasException) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                binding.noResult.text = getString(R.string.failed_to_load_data)
                binding.noResult.visibility = View.VISIBLE
                searchViewModel.resetException()
            }
        }

        searchViewModel.isLoading.observe(this) { isLoading ->
            binding.progBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvUpcomingEvent.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.noResult.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }
}
