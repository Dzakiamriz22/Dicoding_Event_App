package com.example.eventdicoding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventdicoding.data.repository.EventRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {

    private val creators: Map<Class<out ViewModel>, () -> ViewModel> = mapOf(
        HomeViewModel::class.java to { HomeViewModel(eventRepository) },
        UpcomingViewModel::class.java to { UpcomingViewModel(eventRepository) },
        FinishedViewModel::class.java to { FinishedViewModel(eventRepository) },
        SearchViewModel::class.java to { SearchViewModel(eventRepository) },
        DetailViewModel::class.java to { DetailViewModel(eventRepository) },
        FavoriteViewModel::class.java to { FavoriteViewModel(eventRepository) }
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        return creator() as T
    }
}
