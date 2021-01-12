package com.example.reslivtest.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reslivtest.util.database.DataBaseLocationConverter
import com.example.reslivtest.util.network.RetrofitInstance
import com.example.reslivtest.util.network.WeatherResponse
import com.example.reslivtest.util.weather_response.WeatherCall
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LocationHelper(
    val context: Context
) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var weatherLiveData: MutableLiveData<WeatherResponse<WeatherCall?>?>? = null

    @SuppressLint("MissingPermission")
    fun updateLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (LocationUtility.hasLocationPermission(context)) {
            val request = LocationRequest().apply {
                interval = 5000
                fastestInterval = 1000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }


    private val locationCallback = object : LocationCallback() {
        @SuppressLint("SetTextI18n")
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            if (locationResult.locations.isNotEmpty()) {
                val latitude = locationResult.locations[0].latitude
                val longitude = locationResult.locations[0].longitude
                loadWeather(latitude, longitude)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    fun loadWeather(
        latitude: Double,
        longitude: Double
    ): LiveData<WeatherResponse<WeatherCall?>?>? {
        weatherLiveData = MutableLiveData<WeatherResponse<WeatherCall?>?>()
        fetchData(latitude = latitude, longitude)
        return weatherLiveData
    }


    private suspend fun getData(latitude: Double, longitude: Double) =
        RetrofitInstance().weatherAPI.getWeatherByLocation(
            latitude, longitude,
            lang = lang,
            units = units,
            Constants.KEY_WEATHER
        )


    private fun fetchData(
        latitude: Double,
        longitude: Double
    ): LiveData<WeatherResponse<WeatherCall?>?>? {
        weatherLiveData = MutableLiveData<WeatherResponse<WeatherCall?>?>()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = getData(
                    latitude = latitude,
                    longitude = longitude
                )
                weatherLiveData?.postValue(handleWeatherResponse(response))
                DataBaseLocationConverter(context)
                    .convertDataToDataBase(
                        handleWeatherResponse(response).data
                    )
            }
        } catch (e: Exception) {
            weatherLiveData?.postValue(WeatherResponse.Error(e.message))
        }
        return weatherLiveData
    }


    private fun handleWeatherResponse(response: Response<WeatherCall>?): WeatherResponse<WeatherCall?> {
        if (response!!.isSuccessful) {
            response.body()?.let { resultResponse ->
                return WeatherResponse.Success(resultResponse)
            }
        }
        return WeatherResponse.Error(response.message())
    }


    companion object {
        const val lang = "ru"
        const val units = "metric"
    }

}