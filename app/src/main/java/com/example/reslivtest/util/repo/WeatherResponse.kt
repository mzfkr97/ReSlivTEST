package com.example.reslivtest.util.repo

sealed class WeatherResponse<T> (
    val data: T? = null,
    val message: String? = null
)
{
    class Success<T>(data: T) : WeatherResponse<T>(data)
    class Error<T>(message: String?, data: T? = null) : WeatherResponse<T>(data, message)
    class Loading<T> : WeatherResponse<T>()

}
