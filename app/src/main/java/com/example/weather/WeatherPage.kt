package com.example.weather

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.api.NetworkResponse
import com.example.weather.api.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun WeatherPage(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {

    var city by remember { mutableStateOf("") }

    val weatherResult = viewModel.weatherResult.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = {
                    city = it
                },
                label = { Text("City") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = "City")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search, // ** Go to next **
                    keyboardType = KeyboardType.Text,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Fetch weather data for the city
                        CoroutineScope(GlobalScope.coroutineContext).launch {
                            viewModel.fetchWeather(city)
                            keyboardController?.hide()
                        }

                    }
                )
            )
            IconButton(onClick = {
                // Fetch weather data for the city
                CoroutineScope(GlobalScope.coroutineContext).launch {
                    viewModel.fetchWeather(city)
                    keyboardController?.hide()
                }
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        when (val result = weatherResult.value) {
            is NetworkResponse.Success -> {
                WeatherDetails(data = result.data)
            }

            is NetworkResponse.Error -> {
                Text(text = result.message)
            }

            is NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }

            null -> Text(text = "Enter a city to fetch weather data")
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn, contentDescription = "Location",
                modifier = Modifier.size(40.dp)
            )
            Text(text = data.location.name, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 16.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.current.feelslike_c}°C", fontSize = 32.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(700)
        )

        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Weather icon"
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.current.condition.text, fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Humidity", "${data.current.humidity}%")
                    WeatherKeyVal("Wind", "${data.current.wind_kph} km/h")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Pressure", "${data.current.pressure_mb} hPa")
                    WeatherKeyVal("UV Index", "${data.current.uv}")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Local Time", data.location.localtime.split(" ")[1])
                    // convert 2024-05-30 to 30-05-2024
                    WeatherKeyVal("Date",
                        data.location.localtime.split(" ")[0].split("-").reversed()
                            .joinToString("-")
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherKeyVal(key: String, value: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight(700), color = Color.Black)
        Text(text = key, fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight(500))
    }
}