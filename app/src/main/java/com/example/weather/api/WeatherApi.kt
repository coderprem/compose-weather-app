package com.example.weather.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") key: String,
        @Query("q") city: String
    ): Response<WeatherResponse>
}