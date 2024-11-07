package com.example.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _eventsByKeyword = MutableLiveData<List<Event>>()
    val eventsByKeyword: LiveData<List<Event>> = _eventsByKeyword

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> = _exception

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getEventsByKeyword(keyword: String) {
        if (keyword.isBlank()) return
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = eventRepository.searchEventsByKeyword(keyword).listEvents
                _eventsByKeyword.postValue(events)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetException() {
        _exception.value = false
    }
}
