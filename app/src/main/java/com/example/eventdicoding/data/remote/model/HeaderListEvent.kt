package com.example.eventdicoding.data.remote.model

data class HeaderListEvent(
    val error: Boolean,
    val message: String,
    val listEvents: List<Event>
)
