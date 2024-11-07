package com.example.eventdicoding.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.eventdicoding.R
import com.example.eventdicoding.adapters.FavoriteAdapter
import com.example.eventdicoding.data.local.LocalDatabase
import com.example.eventdicoding.data.remote.api.ApiClient
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentFavoriteBinding
import com.example.eventdicoding.viewmodel.FavoriteViewModel
import com.example.eventdicoding.viewmodel.ViewModelFactory

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private val binding by viewBinding(FragmentFavoriteBinding::bind)
    private val favoriteViewModel: FavoriteViewModel by lazy { createViewModel() }

    private val favoriteAdapter: FavoriteAdapter by lazy {
        FavoriteAdapter(findNavController())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeFavoriteEvents()
        fetchFavoriteEvents()
    }

    private fun createViewModel(): FavoriteViewModel {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(requireActivity()).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        return ViewModelProvider(this, factory)[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerView() {
        with(binding.rvFavorite) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteAdapter
        }
    }

    private fun fetchFavoriteEvents() {
        favoriteViewModel.getFavoriteEvents()
        toggleUIState(isLoading = true)
    }

    private fun observeFavoriteEvents() {
        favoriteViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            favoriteAdapter.submitList(events)

            // Update the UI based on the data state
            toggleUIState(isLoading = false, isEmpty = events.isEmpty())
        }
    }

    private fun toggleUIState(isLoading: Boolean, isEmpty: Boolean = false) {
        with(binding) {
            progBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            tvNoFavoriteEvent.visibility = if (isEmpty) View.VISIBLE else View.GONE

            rvFavorite.visibility = if (!isEmpty && !isLoading) View.VISIBLE else View.GONE
        }
    }
}

