package com.geeks.weatherapp.view.fragments

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.geeks.weatherapp.databinding.FragmentWeatherBinding
import com.geeks.weatherapp.model.models.WeatherResponse
import com.geeks.weatherapp.model.models.data.HourWeather
import com.geeks.weatherapp.model.models.data.WeekWeather
import com.geeks.weatherapp.viewmodel.WeatherViewModel
import com.geeks.weatherapp.view.adapters.HourWeatherAdapter
import com.geeks.weatherapp.view.adapters.WeekWeatherAdapter

class WeatherFragment : Fragment() {

    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels()

    private val data = listOf("Бишкек", "Ош", "Дубай", "Турин", "Якутск")

    private val citiesMap = mapOf(
        "Бишкек" to "Bishkek",
        "Ош" to "Osh KG",
        "Дубай" to "Dubai",
        "Турин" to "Torino Italy",
        "Якутск" to "Yakutsk"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)

        setupSpinner()
        observeViewModel()

        return binding.root
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            data
        ).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
        binding.citySpinner.adapter = adapter

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selectedCity = parent.getItemAtPosition(position).toString()
                val queryValue = citiesMap[selectedCity] ?: selectedCity
                viewModel.fetchWeather(queryValue) // Используем ViewModel
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun observeViewModel() {
        viewModel.weatherLiveData.observe(viewLifecycleOwner) { response ->
            updateUi(response)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun updateUi(weatherResponse: WeatherResponse) {
        val hourWeatherList = weatherResponse.forecast?.forecastday?.get(0)?.hour?.map {
            HourWeather(
                time = it?.time?.split(" ")?.get(1) ?: "N/A",
                temperature = it?.tempC ?: 0.0,
                iconUrl = it?.condition?.icon ?: ""
            )
        } ?: emptyList()

        val weekWeatherList = weatherResponse.forecast?.forecastday?.map { forecastDay ->
            WeekWeather(
                temperatureMax = forecastDay?.day?.maxtempC,
                temperatureMin = forecastDay?.day?.mintempC,
                iconUrl = forecastDay?.day?.condition?.icon,
                day = forecastDay?.date.toString()
            )
        } ?: emptyList()



        binding.rvTodayForecast.adapter = HourWeatherAdapter(hourWeatherList)
        binding.rvWeekWeather.adapter = WeekWeatherAdapter(weekWeatherList)
        binding.rvTodayForecast.adapter?.notifyDataSetChanged()

        binding.apply {
            tvWeatherTemperature.text = "${weatherResponse.current?.tempC}º"
            tvFeelsTemperature.text = "Чувствуется как: ${weatherResponse.current?.feelslikeC}º"
            tvRainy.text = "${weatherResponse.current?.cloud}%"
            tvHumidity.text = "${weatherResponse.current?.humidity}%"
            tvWindSpeed.text = "${weatherResponse.current?.windKph} км/ч"
            weatherType.text = weatherResponse.current?.condition?.text ?: ""
            tvTodayDate.text = weatherResponse.location?.localtime ?: ""
        }
    }
}
