package com.example.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinishedViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<Event>>()
    val finishedEvents: LiveData<List<Event>> = _finishedEvents

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> = _exception

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshLoading = MutableLiveData<Boolean>()
    val isRefreshLoading: LiveData<Boolean> = _isRefreshLoading

    private val _refreshException = MutableLiveData<Boolean>()
    val refreshException: LiveData<Boolean> = _refreshException

    fun loadFinishedEvents() {
        if (_finishedEvents.value == null) {
            _isLoading.value = true
            fetchFinishedEvents()
        }
    }

    fun refreshFinishedEvents() {
        _isRefreshLoading.value = true
        fetchFinishedEvents(true)
    }

    private fun fetchFinishedEvents(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = eventRepository.fetchFinishedEvents().listEvents
                if (isRefresh) {
                    _finishedEvents.postValue(events)
                } else {
                    _finishedEvents.postValue(events)
                }
            } catch (e: Exception) {
                if (isRefresh) {
                    _refreshException.postValue(true)
                } else {
                    _exception.postValue(true)
                }
            } finally {
                if (isRefresh) {
                    _isRefreshLoading.postValue(false)
                } else {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun resetExceptionFlags() {
        _exception.value = false
        _refreshException.value = false
    }
}
