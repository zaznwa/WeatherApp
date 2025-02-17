package com.geeks.weatherapp.presenter

import com.geeks.weatherapp.model.models.WeatherResponse

interface WeatherContract {
    interface View {
        fun showWeather(weatherResponse: WeatherResponse)
        fun showError(message:String)
    }
    interface Presenter{
        fun loadData(location:String)
        fun onDestroy()
    }
}