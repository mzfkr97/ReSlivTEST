package com.example.reslivtest.ui.home


import androidx.lifecycle.*
import com.example.reslivtest.util.database.LocationData
import com.example.reslivtest.util.repo.WeatherResponse
import com.example.reslivtest.util.weather_response.WeatherCall
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(
    private val repository: MainRepository
) : ViewModel()
{

    val allLocationListLiveData = repository.getAllLocation()
    val locationLiveData = MutableLiveData<LatLng>()
    private var weatherLiveData: MutableLiveData<WeatherResponse<WeatherCall?>?>? = null

    private val locationIdLiveData = MutableLiveData<Long>()

    var lastLocationLiveData: LiveData<LocationData?> =
            Transformations.switchMap(locationIdLiveData) { locationId ->
                repository.getLastLocation(locationId)
            }

    fun loadLocationFromId(locationId: Long) {
        locationIdLiveData.value = locationId
    }

    fun saveCurrentLocation(locationData: LocationData) {
        repository.saveLocation(locationData)
    }

    fun loadLocationFromMap(latLng: LatLng){
        locationLiveData.postValue(latLng)
    }


    fun loadWeather(latitude : Double, longitude : Double): LiveData<WeatherResponse<WeatherCall?>?>? {
        weatherLiveData = MutableLiveData<WeatherResponse<WeatherCall?>?>()
        getWeatherList(latitude = latitude, longitude = longitude)
        return weatherLiveData
    }

    private fun getWeatherList(latitude : Double, longitude : Double) = viewModelScope.launch {
        try {
            weatherLiveData?.postValue(WeatherResponse.Loading())
            val response = repository.requestWeatherFromLocation(latitude = latitude, longitude = longitude)
            weatherLiveData?.postValue(handleWeatherResponse(response))
        } catch (e: Exception) {
            weatherLiveData?.postValue(WeatherResponse.Error(e.message))
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