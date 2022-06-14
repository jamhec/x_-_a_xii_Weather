package com.example.weatherapp

import com.example.weatherapp.APIResponse.*
import com.google.gson.Gson
import io.reactivex.rxjava3.kotlin.subscribeBy
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import strikt.api.expectThat


@RunWith(JUnit4::class)
class RetroApiInterfaceUnitTest {

    lateinit var inter: RetroApiInterface
    lateinit var mockServer: MockWebServer
    lateinit var gson : Gson

@Mock
lateinit var fakeWeather: AllWeather



    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        mockServer = MockWebServer()

        gson = Gson()
        inter = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(RetroApiInterface::class.java)

    }
    //Gradle Is hanging with these tests
    @Test
    fun getCurrentWeatherTest(){

            var mockRes = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
            mockServer.enqueue(mockRes)


           val res = inter.getCurrentWeather("0","0","0","0")
            val req = mockServer.takeRequest()

            res.subscribeBy(
                onNext = {
                    assertEquals(true, it != null)
                },
                onError = { println("Error: $it")}
            )
            assertEquals("onecal?lat=0&lon=0&appid=0&units=0", req?.path)

    }
//    @Test
//    fun `it should GET with query`() {
//
//        val givenSearchQuery = "0"
//
//        val request: RecordedRequest = takeMockRequest {
//            getCurrentWeather(givenSearchQuery,givenSearchQuery,
//                givenSearchQuery,givenSearchQuery)
//                .subscribe()
//        }
//
//        expectThat(request) {
//            assertThat("is GET method") {
//                it.method == "GET"
//            }
//            assertThat("has given search query") {
//                it.requestUrl!!.queryParameterValues("lat") == listOf("0")
//            }
//        }
//    }
//
//    fun takeMockRequest(sut: RetroApiInterface.() -> Unit): RecordedRequest {
//        return MockWebServer()
//            .use {
//                it.enqueue(MockResponse())
//                it.start()
//                val url = it.url("/")
//
//                sut(com.example.weatherapp.RetroApiInterface(url))
//
//                it.takeRequest()
//            }
//    }
//    @Test
//    fun getGeolocationTest(){
//
//            var mockRes = MockResponse().setBody("{}")
//
//            mockServer.enqueue(mockRes)
//
//            val res = inter.getGeoloaction("0")
//            val req = mockServer.takeRequest()
//
//
//            assertEquals("https://www.googleapis.com/geolocation/v1/geolocate?key=0",
//                req.path)
//
//    }
//
//    @Test
//    fun getCurrentCityTest(){
//
//            var mockRes = MockResponse().setBody("{}")
//
//            mockServer.enqueue(mockRes)
//
//            val res = inter.getCurrentCity("0","0")
//            val req = mockServer.takeRequest()
//
//
//            assertEquals("https://api.opencagedata.com/geocode/v1/json?q=0&key=0",
//                req.path)
//
//    }

    @After
    fun destroy() {
        mockServer.shutdown()
    }
}