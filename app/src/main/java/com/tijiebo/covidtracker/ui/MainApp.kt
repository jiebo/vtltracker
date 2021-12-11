package com.tijiebo.covidtracker.ui

import android.app.Application
import com.tijiebo.covidtracker.core.di.cacheModule
import com.tijiebo.covidtracker.core.di.networkModule
import com.tijiebo.covidtracker.core.di.repoModule
import com.tijiebo.covidtracker.core.di.vmModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            EmptyLogger()
            androidContext(this@MainApp)
            modules(
                listOf(
                    networkModule,
                    vmModule,
                    repoModule,
                    cacheModule
                )
            )
        }
    }
}