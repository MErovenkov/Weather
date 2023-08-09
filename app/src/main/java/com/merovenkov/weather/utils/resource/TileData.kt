package com.merovenkov.weather.utils.resource

import android.graphics.Bitmap

data class TileData(val bitmap: Bitmap,
                    val zoom: Int,
                    val x: Int,
                    val y: Int)