package com.example.example

import com.google.gson.annotations.SerializedName


data class Daily (

  @SerializedName("dt"      ) var dt      : Long?               = null,
  @SerializedName("temp"    ) var temp    : Temp?              = Temp(),
  @SerializedName("weather" ) var weather : ArrayList<Weather> = arrayListOf()

)