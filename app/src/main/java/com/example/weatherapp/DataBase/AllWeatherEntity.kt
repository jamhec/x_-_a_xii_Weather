package com.example.weatherapp.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "allWeather")
data class AllWeatherEntity(
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val date: String,
    val feelsLike: String,
    val weatherType: String,
    val sunrise: String,
    val sunset: String,
    val humidity: String,
    val windSpeed: String

){
    @PrimaryKey(autoGenerate = false)
    var id:Int = 0
}


