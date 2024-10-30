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
    private val favoriteViewModel by lazy { createViewModel() }
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        fetchFavoriteEvents()
    }

    override fun onResume() {
        super.onResume()
        fetchFavoriteEvents()
    }

    private fun createViewModel(): FavoriteViewModel {
        val eventRepository = EventRepository(
            ApiClient.apiClient,
            LocalDatabase.getInstance(requireActivity()).eventDao()
        )
        val factory = ViewModelFactory(eventRepository)
        return ViewModelProvider(requireActivity(), factory)[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(listOf(), findNavController())
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFavorite.adapter = favoriteAdapter
    }

    private fun fetchFavoriteEvents() {
        favoriteViewModel.getFavoriteEvents()
    }

    private fun setupObservers() {
        favoriteViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            favoriteAdapter.updateData(events)
            updateUI(events.isEmpty())
        }
    }

    private fun updateUI(isEmpty: Boolean) {
        binding.tvNoFavoriteEvent.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.progBar.visibility = View.GONE
    }
}
