package com.example.reslivtest.util.database

import android.content.Context
import com.example.reslivtest.util.weather_response.WeatherCall
import java.util.concurrent.Executors


class DataBaseLocationConverter(
    var context: Context
) {

    private val executor = Executors.newSingleThreadExecutor()

    fun convertDataToDataBase(resultResponse: WeatherCall?) {
        val executor = Executors.newSingleThreadExecutor()
        val latitude = resultResponse?.coord?.lat
        val longitude = resultResponse?.coord?.lon
        val iconUrl =
            "http://openweathermap.org/img/wn/" + resultResponse?.weather!![0].icon + "@2x.png"
        val data = LocationResponse(
            1,
            name = resultResponse.name,
            latitude = latitude!!,
            longitude = longitude!!,
            description = resultResponse.description,
            icon = iconUrl,
            temp = resultResponse.main?.temp,
            country = resultResponse.sys?.country,
            sunrise = resultResponse.sys?.sunrise,
            sunset = resultResponse.sys?.sunset,
            windSpeed = resultResponse.wind?.speed
        )

        val database = CityDatabase(context)
        val weatherDao = database.cityDao()
        executor.execute {
            weatherDao.saveLocationResponse(data)
        }

    }


}