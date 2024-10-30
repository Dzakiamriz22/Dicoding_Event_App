package com.example.eventdicoding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class Event(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageLogo: String,
    val category: String,
    val beginTime: String,
    val endTime: String
)
