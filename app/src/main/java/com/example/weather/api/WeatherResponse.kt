package com.example.weather.api

data class WeatherResponse(
    val current: Current,
    val location: Location
)