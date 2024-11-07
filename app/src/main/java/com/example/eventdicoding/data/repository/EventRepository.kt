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
    suspend fun fetchUpcomingEvents(): HeaderListEvent =
        apiService.getUpcomingEvents(1)

    suspend fun fetchFinishedEvents(): HeaderListEvent =
        apiService.getFinishedEvents(0)

    suspend fun fetchLimitedUpcomingEvents(): HeaderListEvent =
        apiService.getLimitedUpcomingEvents(1, 5)

    suspend fun fetchLimitedFinishedEvents(): HeaderListEvent =
        apiService.getLimitedFinishedEvents(0, 5)

    suspend fun fetchEventById(eventId: Int): HeaderSingleEvent =
        apiService.getEventById(eventId)

    suspend fun searchEventsByKeyword(keyword: String): HeaderListEvent =
        apiService.getEventsByKeyword(-1, keyword)

    suspend fun addEventToFavorites(event: Event) {
        eventDao.addEvent(event)
    }

    suspend fun fetchAllFavoriteEvents(): List<Event> =
        eventDao.fetchAllEvents()

    suspend fun checkIfEventExistsInFavorites(eventId: Int): Boolean {
        val count = eventDao.eventExists(eventId)
        return count > 0
    }

    suspend fun removeEventFromFavorites(eventId: Int) {
        eventDao.removeEvent(eventId)
    }
}
