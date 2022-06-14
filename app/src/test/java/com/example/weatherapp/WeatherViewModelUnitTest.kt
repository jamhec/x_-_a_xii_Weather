package com.example.weatherapp

import com.example.weatherapp.APIResponse.*
import com.example.weatherapp.DataBase.PlaceName
import com.example.weatherapp.DataBase.WeatherDao
import com.example.weatherapp.GeolocationApi.Geolocation
import com.example.weatherapp.GeolocationApi.Location
import com.example.weatherapp.ReverseGeocoding.Components
import com.example.weatherapp.ReverseGeocoding.CurrentCity
import com.example.weatherapp.ReverseGeocoding.Result
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class WeatherViewModelUnitTest {
     lateinit var repo: WeatherRepository
     lateinit var vm: WeatherViewModel

     @Mock
     lateinit var inter: RetroApiInterface

     @Mock
     lateinit var dao: WeatherDao

     @Before
     fun setup(){
         MockitoAnnotations.openMocks(this)
         repo = WeatherRepository(inter, dao)
         vm = WeatherViewModel(repo)
     }

    @Test
    fun getCurrentWeatherTest(){
        runBlocking {
            val fakeWeather = AllWeather(listOf(Alert("Fire in Woods", 2,"Fire",
            "Jessica",1,listOf("fire","danger"))),
                (Current(1,2.2,2,2.2,
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
                listOf(Minutely(2,2.2)),"PST",12345)
            Mockito.`when`(repo.getCurrentWeather("0","0","0","0"))
                .thenReturn(Observable.just(fakeWeather))

            vm.getCurrentWeather("0","0","0","0")
            val result = repo.getCurrentWeather("0","0","0","0")

            result.subscribeBy(
                onNext = {
                    assertEquals(fakeWeather, it)
                },
                onError = { println("Error: $it")}
            )
        }
    }

    @Test
    fun getGeolocationTest(){
        runBlocking {
            val fakeGeolocation = Geolocation(Location(70.0,70.0))

            Mockito.`when`(repo.getGeoloaction("0"))
                .thenReturn(Observable.just(fakeGeolocation))

            vm.getGeoloaction("0")
            val result = repo.getGeoloaction("0")

            result.subscribeBy(
                onNext = {
                    assertEquals(fakeGeolocation, it)
                },
                onError = { println("Error: $it")}
            )
        }
    }

    @Test
    fun getCurrentCityTest(){
        runBlocking {
            val fakeCurrentCity = CurrentCity(listOf(Result(Components(
                "LA","USA", "USA", "LA","CA"))))

            Mockito.`when`(repo.getCurrentCity("70, 70","0"))
                .thenReturn(Observable.just(fakeCurrentCity))

            vm.getCurrentCity("70, 70", "0")
            val result = repo.getCurrentCity("70, 70", "0")

            result.subscribeBy(
                onNext = {
                    assertEquals(fakeCurrentCity, it)
                },
                onError = { println("Error: $it")}
            )
        }
    }

    @Test
    fun getPlaceNameTest(){
        val fakePlaceName :PlaceName = PlaceName("Los Angeles")

        Mockito.`when`(repo.getPlaceName())
            .thenReturn(fakePlaceName)

        val result = vm.getPlaceName()

        assertEquals(fakePlaceName, result)
    }

}