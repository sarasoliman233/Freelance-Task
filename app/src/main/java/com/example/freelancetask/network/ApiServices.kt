package com.example.freelancetask.network

import WeatherResponse
import com.example.example.WeeklyWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("weather")

    fun fetchWeather(@Query("q")city:String, @Query("appid") appKey : String): Call<WeatherResponse>


    @GET("data/2.5/weather")
    fun getWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>

    @GET("data/3.0/onecall")
    fun fetchWeeklyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "current,minutely,hourly,alerts",
        @Query("appid") appKey: String
    ): Call<WeeklyWeather>
}