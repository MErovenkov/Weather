package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.utils.resource.PrecipitationData
import com.example.weather.data.repository.Repository
import com.example.weather.utils.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrecipitationMapViewModel(private val repository: Repository): ViewModel() {

    private var resource: MutableStateFlow<Resource<PrecipitationData>> = MutableStateFlow(Resource(null))

    fun getResource(): StateFlow<Resource<PrecipitationData>> = resource.asStateFlow()

    fun createPrecipitationData(layer: String, zoom: Int, x: Int, y: Int) {
        viewModelScope.launch {
           repository.getPrecipitationData(layer, zoom, x, y).collect {
               resource.value = it
           }
        }
    }
}