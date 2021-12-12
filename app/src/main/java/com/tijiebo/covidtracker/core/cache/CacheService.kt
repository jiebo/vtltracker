package com.tijiebo.covidtracker.core.cache

import com.tijiebo.covidtracker.core.network.model.CountrySnapshot

interface CacheService {

    fun saveData(data: List<CountrySnapshot>)

    fun getAllData(): List<CountrySnapshot>

    fun getCountryData(countryName: String): CountrySnapshot?

    fun clearCache()
}