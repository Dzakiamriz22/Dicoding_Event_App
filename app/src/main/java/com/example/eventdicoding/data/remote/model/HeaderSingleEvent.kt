package com.example.eventdicoding.data.remote.model

data class HeaderSingleEvent(
    val error: Boolean,
    val message: String,
    val event: Event
)