package com.weatherapp.model

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private fun getInitialCities() = List(20) { i ->
        City(name = "Cidade $i", weather = "Carregando clima...")
    }

    private val _cities = getInitialCities().toMutableStateList()
    val cities
        get() = _cities.toList()

    fun remove(city: City) {
        _cities.remove(city)
    }

    fun add(name: String) {
        _cities.add(City(name = name))
    }
}
