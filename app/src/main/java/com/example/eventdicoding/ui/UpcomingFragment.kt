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
import com.example.eventdicoding.adapters.UpcomingAdapter
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentUpcomingBinding
import com.example.eventdicoding.viewmodel.UpcomingViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory
import com.example.eventdicoding.data.remote.model.Event

class UpcomingFragment : Fragment(R.layout.fragment_upcoming) {

    private val binding by viewBinding(FragmentUpcomingBinding::bind)
    private val upcomingViewModel by lazy { createViewModel() }
    private lateinit var upcomingAdapter: UpcomingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
        loadUpcomingEvents()
    }

    private fun createViewModel(): UpcomingViewModel {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(requireActivity()).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        return ViewModelProvider(this, factory).get(UpcomingViewModel::class.java)
    }

    private fun setupRecyclerView() {
        upcomingAdapter = UpcomingAdapter(listOf(), findNavController())
        binding.upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.upcomingEventsRecyclerView.adapter = upcomingAdapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshContainer.setOnRefreshListener {
            upcomingViewModel.refreshUpcomingEvents()
        }
    }

    private fun observeViewModel() {
        upcomingViewModel.apply {
            upcomingEvents.observe(viewLifecycleOwner) { events ->
                updateUpcomingEvents(events)
            }

            exception.observe(viewLifecycleOwner) { hasError ->
                if (hasError) showErrorToast()
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                toggleLoadingIndicator(isLoading)
            }

            isRefreshLoading.observe(viewLifecycleOwner) { isRefreshing ->
                binding.swipeRefreshContainer.isRefreshing = isRefreshing
            }

            refreshException.observe(viewLifecycleOwner) { hasError ->
                if (hasError) showErrorToast()
            }
        }
    }

    private fun loadUpcomingEvents() {
        upcomingViewModel.fetchUpcomingEvents()
    }

    private fun updateUpcomingEvents(events: List<Event>) {
        binding.apply {
            loadFailedMessage.visibility = View.GONE
            upcomingEventsRecyclerView.visibility = View.VISIBLE
        }
        upcomingAdapter.updateEvents(events)
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
        binding.loadFailedMessage.visibility = View.VISIBLE
        upcomingViewModel.resetExceptions()
    }

    private fun toggleLoadingIndicator(isLoading: Boolean) {
        binding.apply {
            upcomingEventsRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
