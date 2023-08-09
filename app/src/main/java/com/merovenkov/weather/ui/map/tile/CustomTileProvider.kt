package com.merovenkov.weather.ui.map.tile

import android.graphics.*
import android.util.Log
import com.merovenkov.weather.utils.resource.TileData
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import java.io.ByteArrayOutputStream
import java.lang.Exception

open class CustomTileProvider: TileProvider {

    companion object {
        private const val TAG = "CustomTileProvider"
    }

    private var tileDataList: ArrayList<TileData> = ArrayList()

    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        var tile: Tile? = null

        val tileData: TileData? = tileDataList.firstOrNull { tileData -> tileData.x == x
                && tileData.y == y && tileData.zoom == zoom}

        if (tileData != null) {
            val stream = ByteArrayOutputStream()

            try {
                val bitmap: Bitmap = adjustColor(tileData.bitmap)

                stream.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                tile = Tile(256, 256, stream.toByteArray())
            } catch (e: Exception) {
                Log.w(TAG, e.stackTraceToString())
            }
        }

        return tile
    }

    private fun adjustColor(bitmap: Bitmap): Bitmap {
        val adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(adjustedBitmap)

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                setScale(0f, 0f, 255f, 1.5f)
            })
        }

        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }

    fun updateTileDataList(tileDataList: ArrayList<TileData>) {
        this.tileDataList = tileDataList
    }
}