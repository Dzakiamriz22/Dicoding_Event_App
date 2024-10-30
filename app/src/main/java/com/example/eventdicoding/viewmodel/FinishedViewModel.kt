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

    fun getFinishedEvents() {
        if (_finishedEvents.value == null) {
            _isLoading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val events = eventRepository.getFinishedEvents().listEvents
                    _finishedEvents.postValue(events)
                } catch (e: Exception) {
                    _exception.postValue(true)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun refreshFinishedEvents() {
        _isRefreshLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = eventRepository.getFinishedEvents().listEvents
                _finishedEvents.postValue(events)
            } catch (e: Exception) {
                _refreshException.postValue(true)
            } finally {
                _isRefreshLoading.postValue(false)
            }
        }
    }

    fun resetExceptionValues() {
        _exception.value = false
        _refreshException.value = false
    }
}
