package com.example.reslivtest.ui.city

import androidx.lifecycle.LiveData
import com.example.reslivtest.util.Constants
import com.example.reslivtest.util.database.CityData
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.network.RetrofitInstance
import java.util.concurrent.Executors

class CityRepository(
        val database: CityDatabase
        ) {

    private val executor = Executors.newSingleThreadExecutor()
    private val weatherDao = database.cityDao()

    fun getAllCityList(): LiveData<List<CityData>> = weatherDao.getAllCity()

    fun addCity(city: CityData) {
        executor.execute {
            weatherDao.addCity(city)
        }
    }

    fun deleteCity(city: CityData) {
        executor.execute {
            weatherDao.delete(city)
        }
    }

    suspend fun requestWeatherFromLCityName(cityName: String) =
        RetrofitInstance().weatherAPI.getWeatherByCityName(
            cityName,
            lang = lang,
            units = units,
            Constants.KEY_WEATHER
        )


    companion object {
        const val lang = "ru"
        const val units = "metric"

    }
}