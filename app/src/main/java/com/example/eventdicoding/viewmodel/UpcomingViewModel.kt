package com.example.eventdicoding.viewmodel

import androidx.lifecycle.*
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpcomingViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<Event>>()
    val upcomingEvents: LiveData<List<Event>> get() = _upcomingEvents

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> get() = _exception

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshLoading = MutableLiveData<Boolean>()
    val isRefreshLoading: LiveData<Boolean> get() = _isRefreshLoading

    private val _refreshException = MutableLiveData<Boolean>()
    val refreshException: LiveData<Boolean> get() = _refreshException

    fun fetchUpcomingEvents() {
        if (_upcomingEvents.value.isNullOrEmpty()) {
            _isLoading.value = true
            loadUpcomingEvents()
        }
    }

    fun refreshUpcomingEvents() {
        _isRefreshLoading.value = true
        loadUpcomingEvents()
    }

    private fun loadUpcomingEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = eventRepository.fetchUpcomingEvents().listEvents
                if (_isRefreshLoading.value == true) {
                    _isRefreshLoading.postValue(false)
                } else {
                    _upcomingEvents.postValue(events)
                }
                _exception.postValue(false)
            } catch (e: Exception) {
                handleError()
            } finally {
                if (_isRefreshLoading.value != true) {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    private fun handleError() {
        if (_isRefreshLoading.value == true) {
            _refreshException.postValue(true)
        } else {
            _exception.postValue(true)
        }
    }

    fun resetExceptions() {
        _exception.value = false
        _refreshException.value = false
    }
}
