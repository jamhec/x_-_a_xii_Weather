package com.example.weatherapp

import androidx.lifecycle.LiveData
import com.example.weatherapp.APIResponse.*
import com.example.weatherapp.GeolocationApi.Geolocation
import com.example.weatherapp.GeolocationApi.Location
import com.example.weatherapp.ReverseGeocoding.Components
import com.example.weatherapp.ReverseGeocoding.CurrentCity
import com.example.weatherapp.ReverseGeocoding.Result
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response.success
import com.example.weatherapp.DataBase.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import junit.framework.TestCase

@RunWith(JUnit4::class)
class WeatherRepositoryUnitTest : TestCase() {

    lateinit var repo: WeatherRepository

    @Mock
    lateinit var inter: RetroApiInterface

    @Mock
    lateinit var dao: WeatherDao

    @Mock
    lateinit var fakeAllWeather : AllWeather

    @Mock
    lateinit var fakeLiveAllWeatherEntity : LiveData<AllWeatherEntity>

    @Mock
    lateinit var fakeLiveCityLatLong: LiveData<CityLatLong>

    @Mock
    lateinit var fakeLiveFavLocations: LiveData<List<FavLocations>>

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        repo = WeatherRepository(inter, dao)

    }

    @Test
    fun getCurrentWeatherTest(){
        runBlocking {
            Mockito.`when`(inter.getCurrentWeather("0","0","0","0"))
                .thenReturn(Observable.just(fakeAllWeather))

            var result = repo.getCurrentWeather("0","0","0","0")

            result.subscribeBy(
                onNext = {
                    assertEquals(fakeAllWeather, it)
                },
                onError = { println("Error: $it")}
            )
        }
    }

    @Test
    fun getGeolocationTest(){
        runBlocking {
            var fakeGeolocation: Geolocation = Geolocation(Location(70.0, 70.0))
            var fakeApi = "0"
            Mockito.`when`(inter.getGeoloaction(fakeApi))
                .thenReturn(Observable.just(fakeGeolocation))

            var result = repo.getGeoloaction(fakeApi)

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
            var fakeCurrentCity: CurrentCity = CurrentCity(listOf<Result>(Result(
                Components("LA", "USA","USA","LA", "CA"))))
            var fakeLatLng = "Place"
            var fakeApi = "0"

            Mockito.`when`(inter.getCurrentCity(fakeLatLng,fakeApi))
                .thenReturn(Observable.just(fakeCurrentCity))

            var result = repo.getCurrentCity(fakeLatLng,fakeApi)

            result.subscribeBy(
                onNext = {
                    assertEquals(fakeCurrentCity, it)
                },
                onError = { println("Error: $it")}
            )
        }
    }

    @Test
    fun getAllWeatherTest(){
        Mockito.`when`(dao.getAllWeather())
            .thenReturn(fakeLiveAllWeatherEntity)

        var result = repo.getAllWeather()

        assertEquals(fakeLiveAllWeatherEntity,result)

    }

    @Test
    fun insertWeatherTest(){
        var fakeWeather: AllWeatherEntity = AllWeatherEntity("33","34",
        "16","12/12/20","36","Sunny","5am","5pm",
        "34%","130kph")

        repo.insertWeather(fakeWeather)

        val result = repo.getAllWeather()
        //Testing insert into Mocked db how do I test
       // assertEquals(fakeWeather, result.value)
    }

    @Test
    fun getLatLongTest() {

        Mockito.`when`(dao.getLatLong())
            .thenReturn(fakeLiveCityLatLong)

        var result = repo.getLatLong()

        assertEquals(fakeLiveCityLatLong, result)
    }

    @Test
    fun insertLatLong(){
        //
    }

    @Test
    fun getPlaceNameTest(){
        var fakePlace : PlaceName = PlaceName("LA")
        Mockito.`when`(dao.getPlaceName())
            .thenReturn(fakePlace)

        var result = repo.getPlaceName()

        assertEquals(fakePlace, result)
    }

    @Test
    fun insertPlaceNameTest(){
        //
    }

    @Test
    fun getFavLocationsListTest(){
        Mockito.`when`(dao.getFavLocationsList())
            .thenReturn(fakeLiveFavLocations)

        var result = repo.getFavLocationsList()

        assertEquals(fakeLiveFavLocations, result)

    }

    @Test
    fun insertFavLocation(){
        //
    }
}