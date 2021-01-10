package com.example.reslivtest.util.weather_response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*


/**
 * docs https://openweathermap.org/api/one-call-api
 * https://api.openweathermap.org/data/2.5/onecall?lat=27.56&lon=53.03&exclude=hourly,daily&lang=RU&appid=5dc3bd6f3df898801a81bd7803e54ccc
 */
@Entity
data class WeatherCall(
    val base: String?,
    val clouds: Clouds?,
    val cod: Int?,
    val coord: Coord?,
    val dt: Long? = 0,
    @PrimaryKey
    val id: Int?,
    val main: Main?,
    val name: String?,
    @SerializedName("sys")
    val sys: Sys?,
    val timezone: Int?,
    val weather: List<Weather>,
    val wind: Wind?)
{
    val iconUrl: String
        get() = "http://openweathermap.org/img/wn/" + weather[0].icon + "@2x.png"

    val description: String?
        get() = weather[0].description?.capitalize(Locale.ROOT)

    val date: Calendar
        get() {
            val date = Calendar.getInstance()
            date.timeInMillis = dt!! * 1000
            return date
        }

    val tempWithDegree: String
        get() = main?.temp?.toInt().toString()
}