package com.example.weatherapp.ui.locations


import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.APIResponse.LocationWeather
import com.example.weatherapp.DataBase.FavLocations
import com.example.weatherapp.DataBase.WeatherDatabase
import com.example.weatherapp.RetroApiInterface
import com.example.weatherapp.WeatherRepository
import com.example.weatherapp.databinding.ActivityLocationsFragmentBinding
import com.example.weatherapp.log.BaseFragment
import com.example.weatherapp.log.DebugTree
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.limited_favorites_inflatable.view.*
import timber.log.Timber


class LocationsFragment : BaseFragment() {

    private var _binding: ActivityLocationsFragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var placesClient: PlacesClient
    val locWeatherList = ArrayList<LocationWeather>()
    val adapter = LocationAdapter(locWeatherList)
    lateinit var locationViewModel: LocationViewModel


    var locationWeather = ArrayList<LocationWeather>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Timber.plant(DebugTree())

        val intr = RetroApiInterface.create()
        val dao = WeatherDatabase.getInstance(this.requireContext())?.weatherDao()!!
        val repo = WeatherRepository(intr, dao)
        locationViewModel = LocationViewModel(repo)
        val googleApi = "AIzaSyAiANxOSE30Kd-izZbZ4PnYIGo6ROppsMs"
//        val weatherApiKey = "863e72223d279e955d713a9437a9e6ce"
        val weatherApiKey = "d911015e54f48d2bf96b5dcaef433a6a"
        var unit = "metric"


        _binding = ActivityLocationsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.favoritesRecycler.adapter = adapter
        binding.favoritesRecycler.layoutManager = LinearLayoutManager(activity)


        binding.addButton.setOnClickListener {
            Places.initialize(context!!, googleApi)
            placesClient = Places.createClient(context!!)
            val AUTOCOMPLETE_REQUEST_CODE = 1

            val fields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .build(this.context!!)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
//        <---End of Codes for Google autocomplete Fragment --->


        locationViewModel.favLocationsList.observe(viewLifecycleOwner) {
            locationWeather.clear()
            for (loc in it) {
                locationViewModel.getCurrentWeather(
                    loc.latitude,
                    loc.longitude,
                    weatherApiKey,
                    unit
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onNext = {
                            locationWeather.add(LocationWeather(it,loc))
                        },
                        onComplete = {
                            locWeatherList.clear()
                            locationWeather.sortByDescending {it.favLocations.id}
                            locWeatherList.addAll(locationWeather)
                            adapter.notifyDataSetChanged()
                        },
                        onError = { e -> println("this is error $e") }

                    )
            }
        }

        //Delete Fav Location
        adapter.setOnItemLongClickListener(object : LocationAdapter.OnItemLongClickListener {
            override fun onItemLongClick(itemView: View) {
                var locId = itemView.fav_item_location_id.text.toString().toInt()
                var locName = itemView.fav_item_location.text.toString()
                var locLat = itemView.fav_item_location_lat.text.toString()
                var locLong = itemView.fav_item_location_long.text.toString()

                var deleteLocation = FavLocations(locId, locName, locLat, locLong)

                AlertDialog.Builder(context)
                    .setTitle("Do you want to remove $locName?")
                    .setNegativeButton("No") { _, _ ->
                        Toast.makeText(context, "$locName not deleted", Toast.LENGTH_LONG).show()
                    }
                    .setPositiveButton("Yes") { _, _ ->
                        locationViewModel.deleteFavLocation(deleteLocation)
                        Toast.makeText(context, "$locName is deleted", Toast.LENGTH_LONG).show()
                    }.show()
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        var placeName = place.address!!.split(",")
                        var shortPlaceName = placeName[0]+", "+placeName[1]
                        var latitude = place.latLng?.latitude.toString()
                        var longitude = place.latLng?.longitude.toString()
                        locationViewModel.insertFavLocation(
                            FavLocations(
                                placeName = shortPlaceName,
                                latitude = latitude,
                                longitude = longitude
                            )
                        )

                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}

