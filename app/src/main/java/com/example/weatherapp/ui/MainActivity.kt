package com.example.weatherapp.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherData
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*886705b4c1182eb1c69f28eb8c520e20*//*512e78e2b9b2a9894ec93ca1ba00e9ce*/

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Rajkot")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchViewCity
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "886705b4c1182eb1c69f28eb8c520e20", "metric")
        response.enqueue(object : Callback<WeatherData> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val responseBody = response.body()
                Log.d("TAG", "onResponse: $response")
                if (response.isSuccessful && responseBody != null) {

                    val temperature = responseBody.main.temp
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val minTemp = responseBody.main.tempMin
                    val maxTemp = responseBody.main.tempMax

                    binding.tvTemperature.text = "$temperature ℃"
                    binding.tvHumidityDigit.text = "$humidity %"
                    binding.tvWindDigit.text = "$windSpeed m/s"
                    binding.tvSunriseDigit.text = time(sunRise)
                    binding.tvSunsetDigit.text = time(sunSet)
                    binding.tvSeaDigit.text = "$seaLevel hpa"
                    binding.tvCondition.text = condition
                    binding.tvWeatherCondition.text = condition
                    binding.tvMaxTemperature.text = "Max Temp: $maxTemp ℃"
                    binding.tvMinTemperature.text = "Min Temp: $minTemp ℃"
                    binding.tvDay.text = dayName(System.currentTimeMillis())
                    binding.tvDate.text = date()
                    binding.tvCityName.text = cityName

                    changeDesignAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {

            }
        })
    }

    private fun changeDesignAccordingToWeatherCondition(conditions: String) {
        when (conditions) {

            "Partly Clouds", "Clouds", "OverCast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.ic_cloud_background)
                binding.lottieAnimation.setAnimation(R.raw.cloud)

            }

            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.ic_sun_backgroud)
                binding.lottieAnimation.setAnimation(R.raw.sun)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.ic_rain_background)
                binding.lottieAnimation.setAnimation(R.raw.rain)
            }

            "Light Show", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.ic_snow_background)
                binding.lottieAnimation.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.ic_sun_backgroud)
                binding.lottieAnimation.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimation.playAnimation()
    }


    //function for set day
    fun dayName(timeStamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    //function for set date
    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    //function for set time
    fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp * 1000)))
    }

}