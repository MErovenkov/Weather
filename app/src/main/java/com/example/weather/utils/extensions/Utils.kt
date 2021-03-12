package com.example.weather.utils.extensions

import android.app.NotificationManager
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.weather.utils.CheckStatusNetwork

fun CheckStatusNetwork.isNetworkAvailable(): Boolean {
    return getNetworkAvailable().value
}

fun Context.cancelNotification(idNotification: Int) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(idNotification)
}

fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat) {
    val initialPadding = Rect(this.paddingLeft, this.paddingTop,
        this.paddingRight, this.paddingBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding)
    }
}

fun View.updateAllPaddingByWindowInserts() {
    this.doOnApplyWindowInsets { mView, insets, padding ->
        mView.updatePadding(top = padding.top + insets.systemWindowInsetTop,
            bottom = padding.bottom + insets.systemWindowInsetBottom,
            left = padding.left + insets.systemWindowInsetLeft,
            right = padding.right + insets.systemWindowInsetRight)

        insets
    }
}