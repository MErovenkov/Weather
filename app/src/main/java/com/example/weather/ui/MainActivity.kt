package com.example.weather.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.isVisible
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.di.component.ActivityComponent
import com.example.weather.ui.navigation.INavigation
import com.example.weather.ui.theme.ChangeableTheme
import com.example.weather.ui.theme.IThemeManager
import com.example.weather.ui.theme.LayoutInflaterFactory2
import com.example.weather.utils.extensions.getActivityComponent
import javax.inject.Inject
import kotlin.math.hypot

class MainActivity: AppCompatActivity(), ActivityComponent.Holder, ChangeableTheme {

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

    private lateinit var binding: ActivityMainBinding

    override val activityComponent: ActivityComponent by lazy {
        getActivityComponent()
    }

    @Inject
    lateinit var navigation: INavigation
    @Inject
    lateinit var themeManager: IThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(
            LayoutInflater.from(this), LayoutInflaterFactory2(delegate)
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityComponent.inject(this)
        setTheme(themeManager.getSavedTheme())
        handleIntent(intent)
    }

    override fun changeTheme(viewSwitcher: View) {
        val imageAnimThemeSwitch = binding.imageAnimThemeSwitch
        val fragmentContainer = binding.fragmentContainer

        if (imageAnimThemeSwitch.isVisible) {
            return
        }

        val  w = fragmentContainer.measuredWidth
        val  h = fragmentContainer.measuredHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        fragmentContainer.draw(canvas)

        imageAnimThemeSwitch.setImageBitmap(bitmap)
        imageAnimThemeSwitch.isVisible = true

        themeManager.setTheme(binding.root)

        val finalRadius = hypot(w.toFloat(), h.toFloat())
        val anim = ViewAnimationUtils.createCircularReveal(fragmentContainer,
            (viewSwitcher.x + viewSwitcher.pivotX).toInt(),
            (viewSwitcher.y + viewSwitcher.pivotY).toInt(),
            0f, finalRadius)

        anim.duration = 650L
        anim.doOnEnd {
            imageAnimThemeSwitch.setImageDrawable(null)
            imageAnimThemeSwitch.isVisible = false
        }
        anim.start()
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