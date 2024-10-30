package com.example.eventdicoding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventdicoding.data.local.entity.Event

@Dao
interface EventDao {

    @Query("SELECT * FROM event ORDER BY beginTime DESC")
    suspend fun fetchAllEvents(): List<Event>

    @Query("SELECT COUNT(*) FROM event WHERE id = :eventId")
    suspend fun eventExists(eventId: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEvent(event: Event)

    @Query("DELETE FROM event WHERE id = :eventId")
    suspend fun removeEvent(eventId: Int)
}
