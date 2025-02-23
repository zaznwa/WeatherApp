package com.geeks.weatherapp.model.repositories

import com.geeks.weatherapp.model.core.RetrofitClient
import com.geeks.weatherapp.model.models.WeatherResponse

class WeatherRepository {
    private val apiKey = "c598adc060934743b02110305251602"

    suspend fun getCurrentWeather(location: String): WeatherResponse =
        RetrofitClient.retrofitClient.getCurrentWeather(apiKey, location)
}