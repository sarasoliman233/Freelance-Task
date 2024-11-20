package com.example.example

import com.google.gson.annotations.SerializedName


data class WeeklyWeather (

  @SerializedName("daily" ) var daily : ArrayList<Daily> = arrayListOf()

)