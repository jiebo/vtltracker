package com.tijiebo.covidtracker.core.cache

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot

class CovidSharedPreferences(
    context: Context,
    private val gson: Gson
) : CacheService {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(DASHBOARD_PREFS, MODE_PRIVATE)

    override fun saveData(data: List<CountrySnapshot>) {
        sharedPreferences.edit()
            .putString(
                DASHBOARD_DATA_KEY,
                gson.toJson(data.subList(0, 4))
            )
            .apply()
    }

    override fun getAllData(): List<CountrySnapshot> {
        return sharedPreferences.getString(DASHBOARD_DATA_KEY, null)?.let {
            val listType = object : TypeToken<List<CountrySnapshot>>() {}.type
            return@let gson.fromJson(it, listType)
        } ?: emptyList()
    }

    override fun getCountryData(countryName: String): CountrySnapshot? {
        return getAllData().firstOrNull { it.countryName == countryName }
    }

    override fun clearCache() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        const val DASHBOARD_PREFS = "dashboard_prefs"
        const val DASHBOARD_DATA_KEY = "dashboard_data_key"
    }
}