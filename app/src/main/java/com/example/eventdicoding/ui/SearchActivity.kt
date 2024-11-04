package com.example.eventdicoding.ui

import android.os.Bundle
import android.os.PersistableBundle
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
        setupButton()
        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    private fun setupButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(listOf())
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
                if (!query.isNullOrEmpty()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun performSearch(query: String) {
        searchViewModel.getEventsByKeyword(query)
        binding.progBar.visibility = View.VISIBLE
    }

    private fun setupObservers() {
        searchViewModel.eventsByKeyword.observe(this) {
            binding.progBar.visibility = View.GONE
            searchAdapter.updateEvents(it)

            if (it.isNotEmpty()) {
                binding.rvUpcomingEvent.visibility = View.VISIBLE
                binding.noResult.visibility = View.GONE
            } else {
                binding.rvUpcomingEvent.visibility = View.GONE
                binding.noResult.text = resources.getString(R.string.no_result)
                binding.noResult.visibility = View.VISIBLE
            }
        }

        searchViewModel.exception.observe(this) {
            if (it) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
                binding.noResult.text = resources.getString(R.string.failed_to_load_data)
                binding.noResult.visibility = View.VISIBLE
                searchViewModel.resetException()
            }
        }

        searchViewModel.isLoading.observe(this) {
            if (it) {
                binding.progBar.visibility = View.VISIBLE
                binding.rvUpcomingEvent.visibility = View.GONE
                binding.noResult.visibility = View.GONE
            } else {
                binding.progBar.visibility = View.GONE
            }
        }
    }
}