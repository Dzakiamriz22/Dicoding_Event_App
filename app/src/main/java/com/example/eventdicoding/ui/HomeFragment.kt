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
import com.example.eventdicoding.adapters.HomeFinishAdapter
import com.example.eventdicoding.adapters.HomeUpcomingAdapter
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
    private lateinit var homeUpcomingAdapter: HomeUpcomingAdapter
    private lateinit var homeFinishedAdapter: HomeFinishAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        setupObservers()
        fetchEvents()
    }

    private fun createViewModel(): HomeViewModel {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(requireActivity()).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        return ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val navController = findNavController()

        homeUpcomingAdapter = HomeUpcomingAdapter(listOf(), navController)
        binding.rvHomeUpcoming.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeUpcoming.adapter = homeUpcomingAdapter

        homeFinishedAdapter = HomeFinishAdapter(listOf(), navController)
        binding.rvHomeFinished.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHomeFinished.adapter = homeFinishedAdapter
    }

    private fun fetchEvents() {
        homeViewModel.getUpcomingAndFinishedEvents()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.refreshUpcomingAndFinishedEvents()
        }
    }

    private fun setupObservers() {
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            handleUpcomingEvents(events)
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            homeFinishedAdapter.updateData(events)
        }

        homeViewModel.exception.observe(viewLifecycleOwner) { hasError ->
            if (hasError) showErrorToast()
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleLoadingIndicator(isLoading)
        }

        homeViewModel.isRefreshLoading.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        homeViewModel.refreshException.observe(viewLifecycleOwner) { hasError ->
            if (hasError) showErrorToast()
        }
    }

    private fun handleUpcomingEvents(events: List<Event>) {
        binding.apply {
            failedLoadData.visibility = View.GONE
            homeUpcoming.visibility = View.VISIBLE
            homeFinished.visibility = View.VISIBLE
            rvHomeUpcoming.visibility = View.VISIBLE
            rvHomeFinished.visibility = View.VISIBLE
        }
        homeUpcomingAdapter.updateEvents(events)
    }

    private fun toggleLoadingIndicator(isLoading: Boolean) {
        binding.apply {
            val visibility = if (isLoading) View.GONE else View.VISIBLE
            homeUpcoming.visibility = visibility
            homeFinished.visibility = visibility
            rvHomeUpcoming.visibility = visibility
            rvHomeFinished.visibility = visibility
            progBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
        binding.failedLoadData.visibility = View.VISIBLE
        homeViewModel.resetExceptionValues()
    }
}
