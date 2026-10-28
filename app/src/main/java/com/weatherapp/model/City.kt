package com.weatherapp.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name: String,
    val weather: String? = null,
    val location: String? = null
)
