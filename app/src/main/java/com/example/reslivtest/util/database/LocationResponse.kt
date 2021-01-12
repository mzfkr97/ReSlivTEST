package com.example.reslivtest.util.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "location_response")
class LocationResponse(
    @PrimaryKey
    val id: Long = 1,
    val name: String?,
    var latitude: Double = 0.00,
    var longitude: Double = 0.00,
    val description: String? = "__ __",
    val icon: String? = " ",
    val temp: Double? = 00.00,
    val country: String? = " ",
    val sunrise: Long? = 0,
    val sunset: Long? = 0,
    val windSpeed: Double? = 0.00

) {
    val iconUrl
        get() = "http://openweathermap.org/img/wn/$icon@2x.png"
    val tempWithDegree: String
        get() = temp?.toInt().toString()
}