package com.suri.weather.model

data class Weather(
    val current: Hour,
    val forecast: Forecast,
    val location: Location
)

