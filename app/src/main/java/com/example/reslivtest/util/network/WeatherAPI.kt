package com.example.reslivtest.util.network

import com.example.reslivtest.util.weather_response.WeatherCall
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    /**
     * { https://openweathermap.org/weather-conditions}
     * mzfkr97@gmail  pass code: 312
     * <p>
     * параметры тут :  https://openweathermap.org/current#parameter
    api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

     * http://api.openweathermap.org/data/2.5/weather?id=621741&units=metric&lang=ru&appid=5dc3bd6f3df898801a81bd7803e54ccc
     * */


    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Response<WeatherCall>

    @GET("weather")
    suspend fun getWeatherLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Response<WeatherCall>



    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String
    ): Response<WeatherCall>

}
