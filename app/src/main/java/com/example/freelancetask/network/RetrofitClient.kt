package com.example.freelancetask.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitClient {
    const val baseUrl ="https://api.openweathermap.org/"
    private val client= OkHttpClient()
    private val interceptor= HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private fun getClient(): OkHttpClient = client.newBuilder().addInterceptor(interceptor as HttpLoggingInterceptor).build()
    private fun getInstance():Retrofit=
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(
            getClient()
        ).build()

    fun getServices():ApiServices= getInstance().create(ApiServices::class.java)
}