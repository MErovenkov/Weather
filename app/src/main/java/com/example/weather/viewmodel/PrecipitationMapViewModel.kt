package com.example.weather.viewmodel

import com.example.weather.utils.resource.TileData
import com.example.weather.data.repository.Repository
import com.example.weather.utils.resource.Resource
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class PrecipitationMapViewModel(private val repository: Repository): BaseViewModel() {
    val resourceTileDataList: PublishRelay<Resource<ArrayList<TileData>>> = PublishRelay.create()
    private val tileDataList: ArrayList<TileData> = ArrayList()

    fun getTileData(layer: String, zoom: Int, x: Int, y: Int) {
        if(!isExistTileData(zoom, x, y)) {
            compositeDisposable.add(repository.getTileData(layer, zoom, x, y)
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer { addTileData(it) })
            )
        }
    }

    private fun addTileData(resourceTileData: Resource<TileData>) {
        resourceTileData.getData()?.let { tileDataList.add(it) }
        resourceTileDataList.accept(
            resourceTileData.getEvent()?.getStatusIfNotHandled()?.let {
                Resource(it, tileDataList) }!!
        )
    }

    private fun isExistTileData(zoom: Int, x: Int, y: Int): Boolean {
        return if (tileDataList.isNullOrEmpty()) {
            false
        } else {
            tileDataList.any { tileData -> tileData.x == x
                                           && tileData.y == y
                                           && tileData.zoom == zoom }
        }
    }

    fun clearTileData() = tileDataList.clear()
}