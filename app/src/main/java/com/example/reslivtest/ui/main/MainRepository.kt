package com.example.reslivtest.ui.main

import androidx.lifecycle.LiveData
import com.example.reslivtest.util.Constants
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.database.LocationData
import com.example.reslivtest.util.network.RetrofitInstance
import java.util.concurrent.Executors


class MainRepository(
        val database: CityDatabase
) {

    private val executor = Executors.newSingleThreadExecutor()
    private val weatherDao = database.cityDao()

    fun saveLocation(locationData: LocationData) {
        executor.execute {
            weatherDao.saveLocation(locationData)
        }
    }

    fun getLastLocation(id: Long): LiveData<LocationData?> = weatherDao.getLastLocation(id)

    fun getAllLocation(): LiveData<List<LocationData>> = weatherDao.getAllLocation()

    suspend fun requestWeatherFromLocation(latitude: Double, longitude: Double) =
            RetrofitInstance().weatherAPI.getWeatherByLocation(
                    latitude, longitude,
                    lang = lang,
                    units = units,
                    Constants.KEY_WEATHER
            )


    companion object {
        const val lang = "ru"
        const val units = "metric"
    }

}
