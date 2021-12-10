package com.tijiebo.covidtracker.core.di

import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel {
        DashboardViewModel(
            repo = get()
        )
    }
}