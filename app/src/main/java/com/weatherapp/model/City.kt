package com.weatherapp.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name: String,
    val weather: String? = null,
    @Contextual val location: LatLng? = null
)
