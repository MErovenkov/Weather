package com.example.weather.utils.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.di.component.ActivityComponent

/**
 * Fragment extension
 */
fun Fragment.hideKeyboard() {
    (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
}

fun Fragment.hasLocationPermission(): Boolean{
    return context?.let {
        ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
    } == PackageManager.PERMISSION_GRANTED
}

fun Fragment.isLocationEnable(): Boolean{
    return (context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        .isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Fragment.showToast(event: Int) {
    if (activity?.hasWindowFocus() == true) {
        Toast.makeText(
            requireContext(),
            this.getString(event),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Fragment.showNoInternetAccess() {
    this.showToast(R.string.no_internet_access)
}

fun Fragment.getActivityComponent(context: Context): ActivityComponent {
    return (context.applicationContext as MyApplication).appComponent.activityComponent().create(context)
}

fun Fragment.getCustomAnim(): NavOptions {
    return navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }
}