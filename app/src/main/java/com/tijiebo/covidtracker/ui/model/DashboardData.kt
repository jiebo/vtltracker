package com.tijiebo.covidtracker.ui.model

import com.tijiebo.covidtracker.core.network.model.CountrySnapshot

data class DashboardData(
    val primaryCountry: CountrySummary,
    val vtlCountries: List<CountrySummary>
) {
    data class CountrySummary(
        val countryName: String,
        val latestConfirmed: Int,
        val latestDeaths: Int,
        val infectionRate: Double
    )

    companion object {
        fun fromCountrySnapshots(snapShots: List<CountrySnapshot>): DashboardData {
            return DashboardData(
                primaryCountry = snapShots.first { it.countryName == "Singapore" }
                    .toCountrySummary(),
                vtlCountries = snapShots.filter {
                    it.countryName != "Singapore" && it.daily.size >= 15
                }.map {
                    it.toCountrySummary()
                }.sortedBy { it.countryName }
            )
        }

    }
}