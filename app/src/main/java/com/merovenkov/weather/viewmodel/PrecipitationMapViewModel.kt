package com.merovenkov.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merovenkov.weather.utils.resource.TileData
import com.merovenkov.weather.data.repository.Repository
import com.merovenkov.weather.utils.extensions.getData
import com.merovenkov.weather.utils.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrecipitationMapViewModel(private val repository: Repository): ViewModel() {

    private var resourceTileDataList: MutableStateFlow<Resource<ArrayList<TileData>>>
        = MutableStateFlow(Resource(ArrayList()))

    fun getResource(): StateFlow<Resource<ArrayList<TileData>>> = resourceTileDataList.asStateFlow()

    fun getTileData(layer: String, zoom: Int, x: Int, y: Int) {
        viewModelScope.launch {
            if(!isExistTileData(zoom, x, y)) {
               repository.getTileData(layer, zoom, x, y).collect {
                   addTileData(it)
               }
           }
        }
    }

    private fun addTileData(resourceTileData: Resource<TileData>) {
        val tmp: ArrayList<TileData> = ArrayList(resourceTileDataList.getData()!!)

        resourceTileData.getData()?.let { tmp.add(it) }

        resourceTileDataList.value =
            resourceTileData.getEvent()?.getStatusIfNotHandled()?.let { Resource(it, tmp) }!!
    }

    private fun isExistTileData(zoom: Int, x: Int, y: Int): Boolean {
        return if (resourceTileDataList.getData().isNullOrEmpty()) {
            false
        } else {
            resourceTileDataList.getData().let { tileDataList ->
                tileDataList!!.any {
                        tileData -> tileData.x == x
                        && tileData.y == y
                        && tileData.zoom == zoom }
            }
        }
    }
}