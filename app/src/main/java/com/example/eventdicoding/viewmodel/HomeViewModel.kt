package com.example.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<Event>>()
    val upcomingEvents: LiveData<List<Event>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<Event>>()
    val finishedEvents: LiveData<List<Event>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun fetchEvents() {
        if (_upcomingEvents.value == null || _finishedEvents.value == null) {
            _isLoading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val upcoming = eventRepository.fetchUpcomingEvents().listEvents
                    val finished = eventRepository.fetchFinishedEvents().listEvents
                    postEvents(upcoming, finished)
                } catch (e: Exception) {
                    _error.postValue(true)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun refreshEvents() {
        _isRefreshing.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val upcoming = eventRepository.fetchUpcomingEvents().listEvents
                val finished = eventRepository.fetchFinishedEvents().listEvents
                postEvents(upcoming, finished)
            } catch (e: Exception) {
                _error.postValue(true)
            } finally {
                _isRefreshing.postValue(false)
            }
        }
    }

    private fun postEvents(upcoming: List<Event>, finished: List<Event>) {
        _upcomingEvents.postValue(upcoming)
        _finishedEvents.postValue(finished)
    }

    fun resetErrorFlags() {
        _error.value = false
    }
}
