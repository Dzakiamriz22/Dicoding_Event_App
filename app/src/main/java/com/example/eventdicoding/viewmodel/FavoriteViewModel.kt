package com.example.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.local.entity.Event
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _favoriteEvents = MutableLiveData<List<Event>>()
    val favoriteEvents: LiveData<List<Event>> = _favoriteEvents

    fun getFavoriteEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val events = eventRepository.fetchAllFavoriteEvents()
            _favoriteEvents.postValue(events)
        }
    }
}
