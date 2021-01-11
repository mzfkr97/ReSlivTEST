package com.example.reslivtest.ui.city

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reslivtest.util.database.CityData
import com.example.reslivtest.util.network.WeatherResponse
import com.example.reslivtest.util.weather_response.WeatherCall
import kotlinx.coroutines.launch
import retrofit2.Response

class CityViewModel(
        private val repository : CityRepository
) : ViewModel() {

    val weatherListLiveData = repository.getAllCityList()
    private var cityIdWeatherLiveData: MutableLiveData<WeatherResponse<WeatherCall?>?>? = null

    fun deleteCity(cityData: CityData) {
        repository.deleteCity(cityData)
    }

    fun addCity(cityData: CityData) {
        repository.addCity(cityData)
    }

    fun loadWeatherFromId(cityName : String): LiveData<WeatherResponse<WeatherCall?>?>? {
        cityIdWeatherLiveData = MutableLiveData<WeatherResponse<WeatherCall?>?>()
        getCityDataFromID(cityName = cityName)
        return cityIdWeatherLiveData
    }

    private fun getCityDataFromID(cityName : String) = viewModelScope.launch {
        try {
            cityIdWeatherLiveData?.postValue(WeatherResponse.Loading())
            val response = repository.requestWeatherFromLCityName(cityName = cityName)
            cityIdWeatherLiveData?.postValue(handleWeatherResponse(response))
        } catch (e: Exception) {
            cityIdWeatherLiveData?.postValue(WeatherResponse.Error(e.message))
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherCall>?): WeatherResponse<WeatherCall?> {
        if (response!!.isSuccessful) {
            response.body()?.let { resultResponse ->
                return WeatherResponse.Success(resultResponse)
            }
        }
        return WeatherResponse.Error(response.message())
    }

}