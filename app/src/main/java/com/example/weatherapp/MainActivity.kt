package com.example.weatherapp


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.log.DebugTree
import com.example.weatherapp.ui.currentlocation.CurrentLocationFragment
import com.example.weatherapp.ui.locations.LocationsFragment
import com.example.weatherapp.ui.weekly.WeeklyFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    val fragmentCurrentLocation = CurrentLocationFragment()
    val fragmentWeekly = WeeklyFragment()
    val fragmentLocation = LocationsFragment()
    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = fragmentCurrentLocation

    var unit ="imperial"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())
        getSupportActionBar()?.hide();

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            supportActionBar!!.title = "Weather App"
        } catch(e : Exception) {
            Timber.e(e)
        }

        try {
            fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragmentLocation, "3").hide(fragmentLocation).commit()
            fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragmentWeekly, "2").hide(fragmentWeekly).commit()
            fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, fragmentCurrentLocation, "1").commit()
            } catch(e : Exception) {
            Timber.e(e)
            }

        val navView: BottomNavigationView = binding.bottomNav

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.currentlocationFragment2, R.id.weeklyFragment2,  R.id.locationsFragment2
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.currentlocationFragment2 ->{
                    fm.beginTransaction().hide(active).show(fragmentCurrentLocation).commit()
                    active = fragmentCurrentLocation
                    true
                }
                R.id.weeklyFragment2 ->{
                    fm.beginTransaction().hide(active).show(fragmentWeekly).commit()
                    active = fragmentWeekly
                    true
                }
                R.id.locationsFragment2 ->{
                    fm.beginTransaction().hide(active).show(fragmentLocation).commit()
                    active = fragmentLocation
                    true
                }
                else -> false
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.degreeUnit) {
            if (unit.equals("imperial")){
                unit = "metric"
                item.setIcon(R.drawable.unit_metric)
                item.setTitle("Metric Units")

            }else{
                unit = "imperial"
                item.setIcon(R.drawable.unit_imperial)
                item.setTitle("Imperial Units")
            }
        }
        return super.onOptionsItemSelected(item)
    }


}


