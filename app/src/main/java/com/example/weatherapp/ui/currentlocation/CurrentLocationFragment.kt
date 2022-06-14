package com.example.weatherapp.ui.currentlocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.weatherapp.DataBase.WeatherDatabase
import com.example.weatherapp.R
import com.example.weatherapp.RetroApiInterface
import com.example.weatherapp.WeatherRepository
import com.example.weatherapp.WeatherViewModel
import com.example.weatherapp.databinding.FragmentCurrentLocationBinding
import com.example.weatherapp.log.BaseFragment
import com.example.weatherapp.log.DebugTree
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CurrentLocationFragment : BaseFragment() {

    private var _binding: FragmentCurrentLocationBinding? = null
    private val binding get() = _binding!!

    lateinit var placesClient: PlacesClient
    lateinit var currentLocationViewModel: WeatherViewModel

    val googleApi = "AIzaSyAiANxOSE30Kd-izZbZ4PnYIGo6ROppsMs" // Google Cloud API

    val weatherApiKey = "dbaa4f9d6a3667a9dc5fd50d2100cc05"
    //   val weatherApiKey = "d911015e54f48d2bf96b5dcaef433a6a"
    //    val weatherApiKey = "863e72223d279e955d713a9437a9e6ce"    // Open Weather API
    val openCageDataKey = "8eb888cd6f6142ee9203998161b2eb7c"  // OpenCage Geocoding API
    var units = "metric"  //imperial

    lateinit  var autocompleteFragment: AutocompleteSupportFragment


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Timber.plant(DebugTree())

        try {

            //throw RuntimeException("Test")
            val intr = RetroApiInterface.create()
            val dao = WeatherDatabase.getInstance(this.requireContext())?.weatherDao()!!
            val repo = WeatherRepository(intr, dao)
            currentLocationViewModel = WeatherViewModel(repo)

        } catch(e : Exception) {
            Timber.e(e)
        }
        _binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        //TODO : update current_degree_metric along with calculations
        //TODO : several gradients depending on weather
        //Geolocation is just lat and long.
        currentLocationViewModel.getGeoloaction(googleApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext={
                    var latitude = it.location.lat.toString()
                    var longitude = it.location.lng.toString()
                    getCurrentWeather(latitude, longitude)
                    getCurrentCity(latitude, longitude)
                },
                onError = {e -> println("this is error $e")}
            )

//<----Codes for Google autocomplete Fragment. More here: https://developers.google.cn/maps/documentation/places/android-sdk/autocomplete--->
        Places.initialize(context!!, googleApi)
        placesClient = Places.createClient(context!!)
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                println("Place: ${place.address}, ${place.latLng}")
                //binding.tvCityName.text = place.address TODO : Please insert this text into the searchbar // searchViewID.setQuery(searchToken, false);
                var latitude = place.latLng?.latitude.toString()
                var longitude = place.latLng?.longitude.toString()
                getCurrentWeather(latitude, longitude)
//                currentLocationViewModel.getCurrentWeather(latitude, longitude, weatherApiKey, units)
                autocompleteFragment.setHint(place.address)
            }

            override fun onError(status: Status) {
                println("An error occurred: $status")
            }
        })
//        <---End of Codes for Google autocomplete Fragment --->


        //changes metric when view is clicked
        //binding.metric_change_button.setOnClickListener {} TODO : Please set an on-click listener to change the metric (Fahrenheit - Celcius)

        return root
    }

    fun setIcon(icon: String): Int{
        var iconNumber = when (icon){
            "01d" -> R.drawable.w_clear_sky_day
            "01n" -> R.drawable.w_clear_sky_night
            "02d" -> R.drawable.w_few_clouds_day
            "02n" -> R.drawable.w_few_clouds_night
            "03d" -> R.drawable.w_scattered_clouds
            "03n" -> R.drawable.w_scattered_clouds
            "04d" -> R.drawable.w_broken_clouds
            "04n" -> R.drawable.w_broken_clouds
            "09d" -> R.drawable.w_shower_rain
            "09n" -> R.drawable.w_shower_rain
            "10d" -> R.drawable.w_rain_day
            "10n" -> R.drawable.w_rain_night
            "11d" -> R.drawable.w_thunderstorm
            "11n" -> R.drawable.w_thunderstorm
            "13d" -> R.drawable.w_snow
            "13n" -> R.drawable.w_snow
            "50d" -> R.drawable.w_mist
            "50n" -> R.drawable.w_mist
            else -> R.drawable.w_clear_sky_day
        }
        return iconNumber
    }

    fun getCurrentWeather(latitude: String, longitude:String){
        currentLocationViewModel.getCurrentWeather(latitude, longitude, weatherApiKey, units)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext ={
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val pJson = gson.toJson(it)
//          println(pJson)

                    // Weather data
                    binding.currentDate.text = SimpleDateFormat("MMM dd").format(it.current.dt.toLong()*1000).toString()
                    binding.currentTime.text = SimpleDateFormat("h:mm a").format(it.current.dt.toLong()*1000).toString()

                    binding.currentTemperature.text = it.current.temp.roundToInt().toString() + "°"
                    binding.currentConditions.text = it.current.weather[0].main//.capitalize()
                    binding.currentIcon.setImageDrawable(context?.getDrawable(setIcon(it.current.weather[0].icon)))


                    binding.currentHumidity.text = it.current.humidity.toString() + "%"
                    binding.currentDewPoint.text = it.current.dewPoint.roundToInt().toString() + "°"
                    binding.currentPressure.text = it.current.pressure.toString() + " hPa"
                    binding.currentUvIndex.text = it.current.uvi.toString()
                    binding.currentVisibility.text = it.current.visibility.toString()+ "m"
                    binding.currentMaxTemp.text = it.daily[0].temp.max.roundToInt().toString() + "°" //detail
                    binding.currentMinTemp.text = it.daily[0].temp.min.roundToInt().toString() + "°" //detail
                    binding.currentFeelsLike.text = it.current.feelsLike.roundToInt().toString() + "°" //detail


                    //12 hourly update textview
                    binding.hourlyIconOne.setImageDrawable(context?.getDrawable(setIcon(it.hourly[1].weather[0].icon)))
                    binding.tempOne.text = it.hourly[1].temp.roundToInt().toString()
                    binding.timeOne.text = SimpleDateFormat("h:mm a").format(Date(it.hourly[1].dt.toLong()*1000))

                    binding.hourlyIconTwo.setImageDrawable(context?.getDrawable(setIcon(it.hourly[2].weather[0].icon)))
                    binding.tempTwo.text = it.hourly[2].temp.roundToInt().toString()
                    binding.timeTwo.text = SimpleDateFormat("h:mm a").format(Date(it.hourly[2].dt.toLong()*1000))

                    binding.hourlyIconThree.setImageDrawable(context?.getDrawable(setIcon(it.hourly[3].weather[0].icon)))
                    binding.tempThree.text = it.hourly[3].temp.roundToInt().toString()
                    binding.timeThree.text = SimpleDateFormat("h:mm a").format(Date(it.hourly[3].dt.toLong()*1000))

                    binding.hourlyIconFour.setImageDrawable(context?.getDrawable(setIcon(it.hourly[4].weather[0].icon)))
                    binding.tempFour.text = it.hourly[4].temp.roundToInt().toString()
                    binding.timeFour.text = SimpleDateFormat("h:mm a").format(Date(it.hourly[4].dt.toLong()*1000))

                    binding.hourlyIconFive.setImageDrawable(context?.getDrawable(setIcon(it.hourly[5].weather[0].icon)))
                    binding.tempFive.text = it.hourly[5].temp.roundToInt().toString()
                    binding.timeFive.text = SimpleDateFormat("h:mm a").format(Date(it.hourly[5].dt.toLong()*1000))

                    //alerts
                    if (it.alerts != null){
                        binding.alertSection.visibility = View.VISIBLE
                        binding.alertEvent.text = it.alerts[0].event.uppercase()
                        binding.alertDescrip.text = it.alerts[0].description
                        binding.alertSender.text = it.alerts[0].senderName

                    }else{
                        binding.alertSection.visibility = View.GONE
                    }

                    setFragmentResult("key_to_weekly", bundleOf("daily" to it.daily))},
                onError = {e -> println("this is error $e")}
            )
    }

    fun getCurrentCity(latitude: String, longitude:String){
        currentLocationViewModel.getCurrentCity("$latitude+$longitude", openCageDataKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext={
                    var city = it.results[0].components.city
                    if (city == null) city = it.results[0].components.county
                    var state = it.results[0].components.state
                    var country = it.results[0].components.country
                    var placeName = "$city, $state, $country"
                    autocompleteFragment.setHint(placeName)
                },
                onError = {e -> println("this is error $e")}
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}