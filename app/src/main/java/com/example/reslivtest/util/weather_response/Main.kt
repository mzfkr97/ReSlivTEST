package com.example.reslivtest.util.weather_response

data class Main(
    val feels_like: Double?,
    val grnd_level: Int?=0,
    val humidity: Int?=0,
    val pressure: Int?=0,
    val sea_level: Int?=0,
    val temp: Double? = 00.00 ,
    val temp_max: Double? = 00.00,
    val temp_min: Double? = 00.00
)