package com.example.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.repository.Repository

abstract class EventStatusViewModel(repository: Repository): ViewModel() {

    private var eventStatus = MediatorLiveData<Int>()

    init {
        eventStatus.addSource(repository.getEventStatus()) {
            eventStatus.postValue(it.getContentIfNotHandled())
        }
    }

    fun getEvent(): LiveData<Int> = eventStatus
}