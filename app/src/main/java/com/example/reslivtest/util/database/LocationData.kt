package com.example.reslivtest.util.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationData(
    @PrimaryKey
    val id: Long = 1,
    var latitude: Double = 0.00,
    var longitude: Double = 0.00
)
