package com.geeks.weatherapp.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.geeks.weatherapp.databinding.FragmentWeatherBinding
import com.geeks.weatherapp.model.models.WeatherResponse
import com.geeks.weatherapp.model.models.data.HourWeather
import com.geeks.weatherapp.presenter.WeatherContract
import com.geeks.weatherapp.presenter.WeatherPresenter
import com.geeks.weatherapp.views.adapters.HourWeatherAdapter


class WeatherFragment : Fragment(), WeatherContract.View {

    private lateinit var binding: FragmentWeatherBinding
    private val presenter by lazy { WeatherPresenter(this) }

    private val data: List<String>
        get() {
            val data = mutableListOf<String>()
            data.add("Бишкек")
            data.add("Ош")
            data.add("Дубай")
            data.add("Турин")
            data.add("Якутск")
            return data
        }

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

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            data
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                presenter.loadData(queryValue)  // Передаем выбранный город
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Обработка случая, когда ничего не выбрано
            }
        }
        return binding.root

    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun showWeather(weatherResponse: WeatherResponse) {

        val hourWeatherList = weatherResponse.forecast?.forecastday?.get(0)?.hour?.map {
            HourWeather(
                time = it?.time?.split(" ")?.get(1)?.substring(0) ?: "N/A",
                temperature = it?.tempC ?: 0.0,
                iconUrl = it?.condition?.icon?:""
            )
        } ?: emptyList()
        binding.rvTodayForecast.adapter = HourWeatherAdapter(hourWeatherList)
        binding.rvTodayForecast.adapter?.notifyDataSetChanged()

        binding.apply {
            tvWeatherTemperature.text = weatherResponse.current?.tempC.toString() + "º"
            tvFeelsTemperature.text =
                "Чувствуется как: " + weatherResponse.current?.feelslikeC.toString() + "º"
            tvRainy.text = weatherResponse.current?.cloud.toString() + "%"
            tvHumidity.text = weatherResponse.current?.humidity.toString() + "%"
            tvWindSpeed.text = weatherResponse.current?.windKph.toString() + "км/ч"
            weatherType.text = weatherResponse.current?.condition?.text.toString()
            tvTodayDate.text = weatherResponse.location?.localtime.toString()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
    }
}