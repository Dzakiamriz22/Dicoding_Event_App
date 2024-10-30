package com.example.eventdicoding.data.repository

import com.example.eventdicoding.data.local.dao.EventDao
import com.example.eventdicoding.data.local.entity.Event
import com.example.eventdicoding.data.remote.api.ApiService
import com.example.eventdicoding.data.remote.model.HeaderListEvent
import com.example.eventdicoding.data.remote.model.HeaderSingleEvent

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {
    suspend fun getUpcomingEvents(): HeaderListEvent {
        return apiService.getUpcomingEvents(1)
    }

    suspend fun getFinishedEvents(): HeaderListEvent {
        return apiService.getFinishedEvents(0)
    }

    suspend fun getLimitUpcomingEvents(): HeaderListEvent {
        return apiService.getLimitedUpcomingEvents(1, 5)
    }

    suspend fun getLimitFinishedEvents(): HeaderListEvent {
        return apiService.getLimitedFinishedEvents(0, 5)
    }

    suspend fun getEventById(id: Int): HeaderSingleEvent {
        return apiService.getEventById(id)
    }

    suspend fun getEventsByKeyword(keyword: String): HeaderListEvent {
        return apiService.getEventsByKeyword(-1, keyword)
    }

    suspend fun saveEventToFavorite(event: Event) {
        eventDao.addEvent(event)
    }

    suspend fun getAllFavoriteEvent(): List<Event> {
        return eventDao.fetchAllEvents()
    }

    suspend fun checkIsEventExistInFavorite(id: Int): Int {
        return eventDao.eventExists(id)
    }

    suspend fun removeEventFromFavorite(id: Int) {
        eventDao.removeEvent(id)
    }
}
