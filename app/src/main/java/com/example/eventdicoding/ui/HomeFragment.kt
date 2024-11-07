package com.example.eventdicoding.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.eventdicoding.R
import com.example.eventdicoding.adapters.EventAdapter
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentHomeBinding
import com.example.eventdicoding.viewmodel.HomeViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory
import com.example.eventdicoding.data.remote.model.Event

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val homeViewModel by lazy { createViewModel() }
    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerViews()
        setupSwipeRefresh()
        observeViewModel()
        loadEvents()
    }

    private fun createViewModel(): HomeViewModel {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(requireActivity()).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        return ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)
    }

    private fun initializeRecyclerViews() {
        val navController = findNavController()

        upcomingAdapter = EventAdapter(navController)
        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
            isNestedScrollingEnabled = true // Pastikan nested scrolling diaktifkan
        }

        finishedAdapter = EventAdapter(navController)
        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
            isNestedScrollingEnabled = true // Pastikan nested scrolling diaktifkan
        }
    }

    private fun loadEvents() {
        homeViewModel.fetchEvents()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.refreshEvents()
        }
    }

    private fun observeViewModel() {
        homeViewModel.apply {
            upcomingEvents.observe(viewLifecycleOwner) { events -> updateUpcomingEvents(events) }
            finishedEvents.observe(viewLifecycleOwner) { events -> finishedAdapter.updateEvents(events) }
            isLoading.observe(viewLifecycleOwner) { isLoading -> updateLoadingState(isLoading) }
            isRefreshing.observe(viewLifecycleOwner) { isRefreshing -> binding.swipeRefresh.isRefreshing = isRefreshing }
            error.observe(viewLifecycleOwner) { showErrorToast() }
        }
    }

    private fun updateUpcomingEvents(events: List<Event>) {
        binding.apply {
            rvUpcomingEvents.visibility = View.VISIBLE
            rvFinishedEvents.visibility = View.VISIBLE
            upcomingAdapter.updateEvents(events)
            failedLoadData.visibility = View.GONE
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.apply {
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            rvUpcomingEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
            rvFinishedEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        binding.failedLoadData.visibility = View.VISIBLE
        homeViewModel.resetErrorFlags()
    }
}
