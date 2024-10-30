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

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> = _exception

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshLoading = MutableLiveData<Boolean>()
    val isRefreshLoading: LiveData<Boolean> = _isRefreshLoading

    private val _refreshException = MutableLiveData<Boolean>()
    val refreshException: LiveData<Boolean> = _refreshException

    fun getUpcomingAndFinishedEvents() {
        if (_upcomingEvents.value == null || _finishedEvents.value == null) {
            _isLoading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val upcomingEvents = eventRepository.getLimitUpcomingEvents().listEvents
                    val finishedEvents = eventRepository.getLimitFinishedEvents().listEvents
                    postEvents(upcomingEvents, finishedEvents)
                } catch (e: Exception) {
                    _exception.postValue(true)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun refreshUpcomingAndFinishedEvents() {
        _isRefreshLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val upcomingEvents = eventRepository.getLimitUpcomingEvents().listEvents
                val finishedEvents = eventRepository.getLimitFinishedEvents().listEvents
                postEvents(upcomingEvents, finishedEvents)
            } catch (e: Exception) {
                _refreshException.postValue(true)
            } finally {
                _isRefreshLoading.postValue(false)
            }
        }
    }

    private fun postEvents(upcomingEvents: List<Event>, finishedEvents: List<Event>) {
        _upcomingEvents.postValue(upcomingEvents)
        _finishedEvents.postValue(finishedEvents)
    }

    fun resetExceptionValues() {
        _exception.value = false
        _refreshException.value = false
    }
}
