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
import com.example.eventdicoding.adapters.EventSearchAdapter
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.ActivitySearchBinding
import com.example.eventdicoding.viewmodel.SearchViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory

class EventSearchActivity : AppCompatActivity(R.layout.activity_search) {

    private val binding by viewBinding(ActivitySearchBinding::bind)
    private val eventSearchViewModel by lazy {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(this).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    private lateinit var eventSearchAdapter: EventSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        setupBackButton()
        setupRecyclerView()
        setupSearchView()
        setupSwipeRefreshLayout()
    }

    private fun setupBackButton() {
        // Correct the button reference
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        eventSearchAdapter = EventSearchAdapter(listOf())
        binding.rvUpcomingEvent.layoutManager = LinearLayoutManager(this)
        binding.rvUpcomingEvent.adapter = eventSearchAdapter
    }

    private fun setupSearchView() {
        binding.searchView.queryHint = "Search for Events"
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.textSize = 14f
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.charcoal_grey))

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performEventSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    eventSearchAdapter.updateEventList(listOf())
                    binding.noResult.visibility = View.GONE // Use 'noResult' here
                    binding.rvUpcomingEvent.visibility = View.GONE
                }
                return false
            }
        })
    }

    private fun performEventSearch(query: String) {
        eventSearchViewModel.getEventsByKeyword(query)
        binding.progBar.visibility = View.VISIBLE
        binding.rvUpcomingEvent.visibility = View.GONE
        binding.noResult.visibility = View.GONE // Use 'noResult' here
    }

    private fun setupObservers() {
        eventSearchViewModel.eventsByKeyword.observe(this) { eventList ->
            binding.progBar.visibility = View.GONE
            if (eventList.isNotEmpty()) {
                eventSearchAdapter.updateEventList(eventList)
                binding.rvUpcomingEvent.visibility = View.VISIBLE
                binding.noResult.visibility = View.GONE // Use 'noResult' here
            } else {
                binding.rvUpcomingEvent.visibility = View.GONE
                binding.noResult.text = getString(R.string.no_result_found)
                binding.noResult.visibility = View.VISIBLE // Use 'noResult' here
            }
        }

        eventSearchViewModel.exception.observe(this) { hasException ->
            if (hasException) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                binding.noResult.text = getString(R.string.failed_to_load_data)
                binding.noResult.visibility = View.VISIBLE // Use 'noResult' here
                eventSearchViewModel.resetException()
            }
        }

        eventSearchViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progBar.visibility = View.VISIBLE
                binding.rvUpcomingEvent.visibility = View.GONE
                binding.noResult.visibility = View.GONE // Use 'noResult' here
            } else {
                binding.progBar.visibility = View.GONE
            }
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            val query = binding.searchView.query.toString()
            if (query.isNotBlank()) {
                performEventSearch(query)
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }
}
