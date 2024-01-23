package com.example.weatherapp.ui


import com.example.weatherapp.data.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
     fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("unit") units: String
    ): Call<WeatherData>


}