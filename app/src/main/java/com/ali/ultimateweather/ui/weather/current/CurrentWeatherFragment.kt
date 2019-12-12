package com.ali.ultimateweather.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ali.ultimateweather.R
import com.ali.ultimateweather.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CurrentWeatherViewModel::class.java)
        bindUI()

//        TODO to be changed to DI: ConnectivityInterceptorImpl(this.context!!)
//        val apiService = WeatherStackApiService(ConnectivityInterceptorImpl(this.context!!))
//        val weatherNetworkDataSource = WeatherNetworkDataSourceImpl(apiService)
//
//        weatherNetworkDataSource.downloadedCurrentWeather.observe(this, Observer {
//            group_loading.visibility = View.GONE
//            tv_feels_like_temperature.text = it.toString()
//        })
//
//        //TODO to be changed later on
//        GlobalScope.launch(Dispatchers.Main) {
//            weatherNetworkDataSource.fetchCurrentWeather("beirut","m");
//        }
    }

    private fun bindUI() = launch {
            val currentWeather = viewModel.weather.await()
            currentWeather.observe(this@CurrentWeatherFragment, Observer {
                if (it == null) return@Observer
                group_loading.visibility = View.GONE
                updateDateToToday()
                updateTemperatures(it.temperature, it.feelslike)
                updateCondition(it.weatherDescriptions.get(0))
                updatePrecipitation(it.precip)
                updateWind(it.windDir, it.windSpeed)
                updateVisibility(it.visibility)
            })
    }

    private fun updateDateToToday() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double) {
        //TODO to be changed based on selected unit
        val unitAbbreviation = "°C"
        tv_temperature.text = "$temperature$unitAbbreviation"
        tv_feels_like_temperature.text = "Feels like $feelsLike$unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        tv_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        //TODO to be changed based on selected unit
        val unitAbbreviation = "mm"
        tv_precipitation.text = "Preciptiation: $precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        //TODO to be changed based on selected unit
        val unitAbbreviation = "kph"
        tv_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        //TODO to be changed based on selected unit
        val unitAbbreviation = "km"
        tv_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }
 }
