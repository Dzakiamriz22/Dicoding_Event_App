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
import com.example.eventdicoding.adapters.FinishedAdapter
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentFinishedBinding
import com.example.eventdicoding.viewmodel.FinishedViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory
import com.example.eventdicoding.data.remote.model.Event

class FinishedFragment : Fragment(R.layout.fragment_finished) {

    private val binding by viewBinding(FragmentFinishedBinding::bind)
    private val finishedViewModel by lazy { createFinishedViewModel() }
    private lateinit var finishedAdapter: FinishedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        setupSwipeRefreshListener()
        observeViewModel()
        loadFinishedEvents()
    }

    private fun createFinishedViewModel(): FinishedViewModel {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(requireActivity()).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        return ViewModelProvider(requireActivity(), factory)[FinishedViewModel::class.java]
    }

    private fun initializeRecyclerView() {
        finishedAdapter = FinishedAdapter(emptyList(), findNavController())
        binding.rvFinished.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFinished.adapter = finishedAdapter
    }

    private fun loadFinishedEvents() {
        finishedViewModel.loadFinishedEvents()
    }

    private fun setupSwipeRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            finishedViewModel.refreshFinishedEvents()
        }
    }

    private fun observeViewModel() {
        finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            displayFinishedEvents(events)
        }

        finishedViewModel.exception.observe(viewLifecycleOwner) { hasError ->
            if (hasError) showErrorToast()
        }

        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            handleLoadingState(isLoading)
        }

        finishedViewModel.isRefreshLoading.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        finishedViewModel.refreshException.observe(viewLifecycleOwner) { hasError ->
            if (hasError) showErrorToast()
        }
    }

    private fun displayFinishedEvents(events: List<Event>) {
        binding.failedLoadData.visibility = View.GONE
        binding.rvFinished.visibility = View.VISIBLE
        finishedAdapter.updateData(events)
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.rvFinished.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
        binding.failedLoadData.visibility = View.VISIBLE
        finishedViewModel.resetExceptionFlags()
    }
}
