package com.example.eventdicoding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventdicoding.data.repository.EventRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(eventRepository) as T
            modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> UpcomingViewModel(eventRepository) as T
            modelClass.isAssignableFrom(FinishedViewModel::class.java) -> FinishedViewModel(eventRepository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(eventRepository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(eventRepository) as T
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> FavoriteViewModel(eventRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
