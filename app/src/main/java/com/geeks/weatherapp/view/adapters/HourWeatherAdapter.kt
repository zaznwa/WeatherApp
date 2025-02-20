package com.geeks.weatherapp.view.adapters
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geeks.weatherapp.databinding.HourWeatherItemBinding
import com.geeks.weatherapp.model.models.data.HourWeather

class HourWeatherAdapter(private val hourWeatherList: List<HourWeather>) : RecyclerView.Adapter<HourWeatherAdapter.HourWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourWeatherViewHolder {
        val binding = HourWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourWeatherViewHolder, position: Int) {
        val hourWeather = hourWeatherList[position]
        holder.bind(hourWeather)
    }

    override fun getItemCount(): Int = hourWeatherList.size

    inner class HourWeatherViewHolder(private val binding: HourWeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(hourWeather: HourWeather) {
            binding.apply {
                tvHourWeatherTime.text = hourWeather.time
                tvHourWeatherTemperature.text = "${hourWeather.temperature}°"

                Glide.with(itemView.context)
                    .load("https:${hourWeather.iconUrl}")
                    .into(ivHourWeatherType)
            }
        }
    }
}