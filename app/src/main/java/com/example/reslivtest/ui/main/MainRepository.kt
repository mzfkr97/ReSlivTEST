package com.example.reslivtest.ui.main

import androidx.lifecycle.LiveData
import com.example.reslivtest.util.Constants
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.database.LocationResponse
import com.example.reslivtest.util.network.RetrofitInstance
import java.util.concurrent.Executors


class MainRepository(
        val database: CityDatabase
) {

    companion object {
        const val lang = "ru"
        const val units = "metric"
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val weatherDao = database.cityDao()

    fun getLastLocation(id: Long): LiveData<LocationResponse?> = weatherDao.getLocationResponseFromId(id)

    fun saveLocationResponse(locationResponse: LocationResponse){
        executor.execute {
            weatherDao.saveLocationResponse(locationResponse)
        }
    }

    suspend fun requestWeatherFromLocation(latitude: Double, longitude: Double) =
            RetrofitInstance().weatherAPI.getWeatherByLocation(
                    latitude, longitude,
                    lang = lang,
                    units = units,
                    Constants.KEY_WEATHER
            )



}
