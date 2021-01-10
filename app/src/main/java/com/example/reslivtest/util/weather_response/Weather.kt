package com.example.reslivtest.util.weather_response

data class Weather(
    val description: String? = "__ __",
    val icon: String? = "",
    val id: Int? = 800,
    val main: String? = ""
){
    val iconUrl: String
        get() = "http://openweathermap.org/img/wn/$icon@2x.png"
}