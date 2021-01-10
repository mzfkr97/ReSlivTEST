package com.example.reslivtest.util.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "city_table")
data class CityData(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var cityName: String = ""
) {

}