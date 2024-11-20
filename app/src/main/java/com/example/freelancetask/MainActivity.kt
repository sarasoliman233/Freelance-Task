package com.example.freelancetask


import WeatherResponse
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.Daily
import com.example.example.WeeklyWeather
import com.example.freelancetask.Adapter.WeeklyWeatherAdapter
import com.example.freelancetask.databinding.ActivityMainBinding
import com.example.freelancetask.network.Constants
import com.example.freelancetask.network.RetrofitClient
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    val requestLocationCallback=registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        getUserLocation()
    }
//   <-............ how to get current location .............. ->

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")

    fun requestPermission(){
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )== PackageManager.PERMISSION_GRANTED){
            getUserLocation()
        }
        else if(
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )){


        }
        else {
            requestLocationCallback.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }


    private fun getUserLocation() {
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Toast.makeText(this,"can access user location",Toast.LENGTH_LONG).show()
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        val bulider= CurrentLocationRequest.Builder()
        bulider.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        bulider.setDurationMillis(500)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.getCurrentLocation(bulider.build(),null).addOnSuccessListener { location: Location?->

            if (location!=null) {
                val latitude = location.latitude
                val longitude = location.longitude

                Log.e("TAG", "getUserLocation:${location?.longitude}")
                Log.e("TAG", "getUserLocation:${location?.latitude}")
                fetchWeatherByLocation(latitude,longitude)
                fetchWeeklyWeather(latitude, longitude)



            }
        }
        fusedLocationClient.lastLocation.addOnFailureListener{
            Log.e("Tag","getuserLocation: ${it.message}")
        }
        val locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,4000).build()
        val callback=object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                Log.e("Location Tag",
                    "onLocation Result : Longitude ${locationResult.lastLocation?.longitude}")
                Log.e("Location Tag",
                    "onLocation Result : Latitude ${locationResult.lastLocation?.latitude}")



            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

    }

    private fun fetchWeeklyWeather(latitude: Double, longitude: Double) {
        val call = RetrofitClient.getServices().fetchWeeklyWeather(latitude, longitude, "current,minutely,hourly,alerts", Constants.apiKey)
        call.enqueue(object : Callback<WeeklyWeather> {
            override fun onResponse(call: Call<WeeklyWeather>, response: Response<WeeklyWeather>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("API Response", "WeeklyWeather: ${it.daily}")
                        updateRecyclerView(it)  // تمرير بيانات الطقس الأسبوعي للـ RecyclerView
                    }
                } else {
                    Log.e("API Error", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<WeeklyWeather>, t: Throwable) {
                Log.e("WeatherApp", "Failed to fetch weekly weather", t)
            }
        })

    }

    private fun updateRecyclerView(response: WeeklyWeather) {

        var weatherList = response.daily.map { daily ->
            val date = convertTimestampToDate(daily.dt!!)
            val temp = daily.temp?.day?.toInt() ?: 0  // درجة الحرارة اليومية
            val icon = daily.weather[0].main?.toLowerCase() ?: "clear"  // وصف الطقس
            WeatherForecast(date, icon, temp)
         
        }


        Log.d("Weather List", "Weather List: $weatherList")


//        val adapter = WeeklyWeatherAdapter(weatherList)
//        binding.weeklyWeatherRv.adapter = adapter
//        adapter.notifyDataSetChanged()

    }


    private fun fetchWeatherByLocation(latitude:Double, longitude:Double){
        val call =RetrofitClient.getServices().getWeatherByLocation(latitude,longitude  ,  Constants.apiKey)
        call.enqueue(object :Callback<WeatherResponse>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(p0: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                response.body()?.let {
                    viewResponse(it)
                }
            }
            override fun onFailure(p0: Call<WeatherResponse>, p1: Throwable) {
            }
        })
    }


    lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

         requestPermission()
//        binding.weeklyWeatherRv.layoutManager = LinearLayoutManager(this)
//        val adapter = WeeklyWeatherAdapter(emptyList()) // تمرير قائمة فارغة في البداية
//        binding.weeklyWeatherRv.adapter = adapter


    }





    private fun convertTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)

        val sdf = SimpleDateFormat("d MMM", Locale.getDefault()) // مثال: "8 Oct"
        return sdf.format(date)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun viewResponse(response: WeatherResponse) {
        val cityName = response.name
        val weatherDescription = response.weather[0].main
        var temp=response.main?.temp!!.toInt()
        var temp2=temp-273
        if (temp2 >= 25) {
            binding.tvTemperature.setTextColor(ContextCompat.getColor(this, R.color.orange)) // لون أحمر
        } else {
            binding.tvTemperature.setTextColor(ContextCompat.getColor(this, R.color.blue)) // لون أخضر
        }
        binding.titleId.text = cityName
       binding.tvTemperature.text=temp2.toString()
        binding.tvCondition.text=response.weather[0].description
        binding.tvDate.text=getDate()


        when (weatherDescription) {
            "Clear" -> binding.iconWeather.setImageResource(R.drawable.clear)
            "Clouds" -> binding.iconWeather.setImageResource(R.drawable.clouds)
            "Rain" -> binding.iconWeather.setImageResource(R.drawable.rainy)
            "Sunny" -> binding.iconWeather.setImageResource(R.drawable.sunny)
            "Cloudy" -> binding.iconWeather.setImageResource(R.drawable.cloudy)
            "Storm" -> binding.iconWeather.setImageResource(R.drawable.storm)
            "Snow" -> binding.iconWeather.setImageResource(R.drawable.snowy)
            "Sun" -> binding.iconWeather.setImageResource(R.drawable.sun)
            "Windy" -> binding.iconWeather.setImageResource(R.drawable.windy)
            else ->binding.iconWeather.setImageResource(R.drawable.clouds)

        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate():String{
        val currentDate = LocalDate.now()
        val dayOfWeek = currentDate.format(DateTimeFormatter.ofPattern("EEEE")) // للحصول على اسم اليوم
        val dayOfMonth = currentDate.dayOfMonth
        val monthName = currentDate.format(DateTimeFormatter.ofPattern("MMMM")) // للحصول على اسم الشهر

        return "$dayOfWeek , $dayOfMonth $monthName"
    }





}