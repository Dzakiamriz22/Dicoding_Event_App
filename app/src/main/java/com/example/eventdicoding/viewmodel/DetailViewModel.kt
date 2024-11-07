package com.example.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _event

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> get() = _exception

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEventExistInFavorite = MutableLiveData<Boolean>()
    val isEventExistInFavorite: LiveData<Boolean> get() = _isEventExistInFavorite

    // Fetch event details
    fun getEvent(id: Int) {
        if (_event.value == null) {
            _isLoading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val event = eventRepository.fetchEventById(id).event
                    _event.postValue(event)
                    _exception.postValue(false)
                } catch (e: Exception) {
                    _exception.postValue(true)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    // Check if the event is in the favorites
    fun checkIsEventExistInFavorite(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val isExist = eventRepository.checkIfEventExistsInFavorites(id) // Now returns Boolean
            _isEventExistInFavorite.postValue(isExist) // Directly set the Boolean value
        }
    }

    // Save event to favorites
    fun saveEventToFavorite(event: com.example.eventdicoding.data.local.entity.Event) {
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.addEventToFavorites(event)
        }
    }

    // Remove event from favorites
    fun removeEventFromFavorite(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.removeEventFromFavorites(id)
        }
    }

    // Reset exception state
    fun resetExceptionValue() {
        _exception.value = false
    }
}
