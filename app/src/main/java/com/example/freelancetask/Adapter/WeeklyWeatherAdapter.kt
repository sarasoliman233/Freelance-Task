package com.example.freelancetask.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freelancetask.R
import com.example.freelancetask.WeatherForecast

class WeeklyWeatherAdapter(private val weatherList:List<WeatherForecast>):RecyclerView.Adapter<WeeklyWeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(view:View):RecyclerView.ViewHolder(view){
        val weekDateText:TextView=view.findViewById(R.id.weekDate)
        val weekIcon:ImageView=view.findViewById(R.id.weekImg)
        val weekTempText:TextView=view.findViewById(R.id.weekTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_desgin, parent, false)
        return WeatherViewHolder(view)

    }

    override fun getItemCount(): Int =weatherList.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]
        Log.d("Adapter", "Binding data at position $position: $weather")
        holder.weekDateText.text = weather.date
        holder.weekTempText.text = "${weather.temp}°C"
        when (weather.icon) {
            "clear" -> holder.weekIcon.setImageResource(R.drawable.clear)
            "clouds" -> holder.weekIcon.setImageResource(R.drawable.clouds)
            "rain" -> holder.weekIcon.setImageResource(R.drawable.rainy)
            "snow" -> holder.weekIcon.setImageResource(R.drawable.snowy)
            "storm" -> holder.weekIcon.setImageResource(R.drawable.storm)
            else -> holder.weekIcon.setImageResource(R.drawable.clouds)

        }

    }


}