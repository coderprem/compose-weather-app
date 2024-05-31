package com.example.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.api.Constant.API_KEY
import com.example.weather.api.NetworkResponse
import com.example.weather.api.RetrofitInstance
import com.example.weather.api.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherResponse>>()
    val weatherResult : LiveData<NetworkResponse<WeatherResponse>> = _weatherResult

    suspend fun fetchWeather(city: String) {

        viewModelScope.launch {
            _weatherResult.value = NetworkResponse.Loading
            // Simulate a network request
            try {
                val response = weatherApi.getCurrentWeather(API_KEY, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Failed to fetch weather data")
                }
            }
            catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather data", e)
                _weatherResult.value = NetworkResponse.Error("Failed to fetch weather data")
            }
        }
    }
}