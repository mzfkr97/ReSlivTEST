package com.example.reslivtest.util.network

import com.example.reslivtest.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    val weatherAPI: WeatherAPI

    init {
        val retrofit: Retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_WEATHER)
                .addConverterFactory( GsonConverterFactory.create())
                .build()
        weatherAPI =
            retrofit.create(WeatherAPI::class.java)
    }


}
