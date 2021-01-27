package com.example.weather.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.ui.navigation.IWeatherNavigation
import com.example.weather.utils.extensions.getActivityComponent
import javax.inject.Inject

class MainActivity: AppCompatActivity() {

    companion object {
        private const val CITY_NAME_KEY = "cityName"
        private const val IS_CURRENT_LOCATION_KEY = "isCurrentLocation"

        fun createIntent(context: Context, cityName: String, isCurrentLocation: Boolean): Intent  {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(CITY_NAME_KEY, cityName)
                putExtra(IS_CURRENT_LOCATION_KEY, isCurrentLocation)
            }
        }
    }

    @Inject
    lateinit var weatherNavigation: IWeatherNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getActivityComponent(this).inject(this)
        openDetailedWeather(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        openDetailedWeather(intent)
    }

    private fun openDetailedWeather(intent: Intent?) {
        intent?.getStringExtra(CITY_NAME_KEY)?.let { nameCity ->
            weatherNavigation.openDetails(nameCity,
                intent.getBooleanExtra(IS_CURRENT_LOCATION_KEY, false),
                hasAnimationOpening = false)
        }
    }
}