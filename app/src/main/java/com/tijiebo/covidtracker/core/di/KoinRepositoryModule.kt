package com.tijiebo.covidtracker.core.di

import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo
import org.koin.dsl.module

val repoModule = module {
    factory {
        CovidTrackerRepo(
            api = get(),
            cache = get()
        )
    }
}