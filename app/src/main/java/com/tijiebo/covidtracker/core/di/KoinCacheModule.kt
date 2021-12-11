package com.tijiebo.covidtracker.core.di

import com.google.gson.Gson
import com.tijiebo.covidtracker.core.cache.CovidSharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModule = module {

    single {
        Gson()
    }

    single {
        CovidSharedPreferences(
            context = androidContext(),
            gson = get()
        )
    }
}