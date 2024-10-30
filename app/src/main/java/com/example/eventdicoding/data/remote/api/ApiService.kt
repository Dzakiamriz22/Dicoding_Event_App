package com.example.eventdicoding.data.remote.api

import com.example.eventdicoding.data.remote.model.HeaderListEvent
import com.example.eventdicoding.data.remote.model.HeaderSingleEvent
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getUpcomingEvents(@Query("active") active: Int): HeaderListEvent

    @GET("events")
    suspend fun getFinishedEvents(@Query("active") active: Int): HeaderListEvent

    @GET("events")
    suspend fun getLimitedUpcomingEvents(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): HeaderListEvent

    @GET("events")
    suspend fun getLimitedFinishedEvents(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): HeaderListEvent

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): HeaderSingleEvent

    @GET("events")
    suspend fun getEventsByKeyword(
        @Query("active") active: Int,
        @Query("q") query: String
    ): HeaderListEvent
}
