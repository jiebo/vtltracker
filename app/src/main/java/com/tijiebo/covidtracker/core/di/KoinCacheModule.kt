package com.tijiebo.covidtracker.core.di

import com.google.gson.Gson
import com.tijiebo.covidtracker.core.cache.CovidSharedPreferences
import com.tijiebo.covidtracker.core.cache.CacheService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModule = module {

    single {
        Gson()
    }

    single<CacheService> {
        CovidSharedPreferences(
            context = androidContext(),
            gson = get()
        )
    }
}