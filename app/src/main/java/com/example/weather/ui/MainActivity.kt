package com.example.weather.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.di.component.ActivityComponent
import com.example.weather.ui.navigation.INavigation
import com.example.weather.utils.extensions.getActivityComponent
import javax.inject.Inject

class MainActivity: AppCompatActivity() {

    companion object {
        private const val CITY_NAME_INDEX = 1
        private const val CITY_PATH = "city"

        private const val CITY_NAME_KEY = "cityName"
        private const val IS_CURRENT_LOCATION_KEY = "isCurrentLocation"

        fun createIntent(context: Context, cityName: String, isCurrentLocation: Boolean): Intent  {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(CITY_NAME_KEY, cityName)
                putExtra(IS_CURRENT_LOCATION_KEY, isCurrentLocation)
            }
        }
    }

    val activityComponent: ActivityComponent by lazy {
        getActivityComponent()
    }

    @Inject
    lateinit var navigation: INavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityComponent.inject(this)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (Intent.ACTION_VIEW == intent?.action) {
            handleDeepLink(intent)
        } else {
            openDetailedWeather(intent)
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        val pathSegments = intent?.data?.pathSegments

        if (!pathSegments.isNullOrEmpty()) {
            if (pathSegments[0] == CITY_PATH && pathSegments.size > 1) {
                navigation.openDetailsByDeepLinkData(pathSegments[CITY_NAME_INDEX])
            }
        }
    }

    private fun openDetailedWeather(intent: Intent?) {
        intent?.getStringExtra(CITY_NAME_KEY)?.let { nameCity ->
            navigation.openDetails(nameCity,
                intent.getBooleanExtra(IS_CURRENT_LOCATION_KEY, false),
                hasAnimationOpening = false)
        }
    }
}