package com.example.freelancetask

import com.example.example.Temp
import com.example.example.Weather

data class WeatherForecast(
    val date: String,
    val icon: String,
    val temp: Int

)

data class DailyWeather(
    val dt: Long,  // التاريخ بصيغة Unix timestamp
    val temp: Temp,
    val weather: List<Weather>
)

