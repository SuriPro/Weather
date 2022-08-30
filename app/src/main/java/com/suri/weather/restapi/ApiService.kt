package com.suri.weather.restapi

import com.suri.weather.model.Weather
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("forecast.json")
    fun getWeather(
        @Query("key") key: String,
        @Query("q") query: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String
    ): Flowable<Weather>


}