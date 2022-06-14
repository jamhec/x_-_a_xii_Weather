package com.example.weatherapp

import com.example.weatherapp.APIResponse.*
import com.example.weatherapp.DataBase.FavLocations
import com.example.weatherapp.GeolocationApi.Location
import com.example.weatherapp.ui.locations.LocationAdapter
import com.example.weatherapp.ui.weekly.WeeklyAdapter
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class LocationAdapterUnitTest {

    lateinit var adapter: LocationAdapter
    lateinit var fakeLocationList: List<LocationWeather>

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        fakeLocationList = listOf(
            LocationWeather(AllWeather(listOf(Alert("Fire in Woods", 2,"Fire",
                "Jessica",1,listOf("fire","danger"))),(Current(1,2.2,2,2.2,
                2,2,2,2,2.2,2.2,2,
                listOf(Weather("Cloudy", "04d", 1, "cloudy")), 2,
                2.2)),
                listOf(Daily(2,2.2,2, FeelsLike(2.2,2.2,2.2,2.2),
                    2,2.2,2,2.2,2,2,Temp(2.2,2.2,2.2,
                        2.2,2.2,2.2),2.2,listOf(Weather("Cloudy",
                        "04d", 2,"cloudy")),2,2.2,2.2)),
                listOf(Hourly(2,2.2,2,2.2,2,2.2,2,
                    Rain(2.2),2.2,2.2,2,
                    listOf(Weather("Cloudy", "04d", 1, "cloudy")),2,
                    2.2,2.2)),2.2,2.2,
                listOf(Minutely(2,2.2)),"PST",12345),
                FavLocations(null,"LA", "70", "70")
            ))
        adapter = LocationAdapter(fakeLocationList)
    }

    @Test
    fun getItemCount(){
        val result = adapter.getItemCount()

        TestCase.assertEquals(fakeLocationList.size, result)
    }
}