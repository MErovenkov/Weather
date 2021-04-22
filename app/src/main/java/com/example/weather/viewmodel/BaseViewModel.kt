package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
}