package com.example.weather.di.module

import com.example.weather.di.component.ActivityComponent
import dagger.Module

@Module(subcomponents = [ActivityComponent::class])
class SubComponentModule