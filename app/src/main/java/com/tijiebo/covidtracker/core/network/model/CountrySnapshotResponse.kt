package com.tijiebo.covidtracker.core.network.model

import com.google.gson.annotations.SerializedName
import com.tijiebo.covidtracker.ui.model.DashboardData
import java.util.*

data class CountrySnapshotResponse(
    @SerializedName("Country") val countryName: String,
    @SerializedName("Confirmed") val confirmed: Int,
    @SerializedName("Deaths") val deaths: Int,
    @SerializedName("Date") val updatedDate: Date
)

data class CountrySnapshot(
    val countryName: String,
    val daily: List<Snapshot>
) {
    data class Snapshot(
        val confirmed: Int,
        val deaths: Int,
        val updatedDate: Date
    )

    fun toCountrySummary(): DashboardData.CountrySummary {
        val latest = daily[daily.size - 1]
        val control = daily[daily.size - 2]
        val currentWeek = daily[daily.size - 1].confirmed - daily[daily.size - 7].confirmed
        val pastWeek = daily[daily.size - 8].confirmed - daily[daily.size - 14].confirmed

        return DashboardData.CountrySummary(
            countryName = countryName,
            latestConfirmed = latest.confirmed - control.confirmed,
            latestDeaths = latest.deaths - control.deaths,
            infectionRate = currentWeek.toDouble() / pastWeek.toDouble()
        )
    }

    companion object {
        fun fromResponse(response: List<CountrySnapshotResponse>): CountrySnapshot {
            val countryName = response.first().countryName
            val snapshots = mutableListOf<Snapshot>()
            for (item in response) {
                snapshots.add(Snapshot(item.confirmed, item.deaths, item.updatedDate))
            }
            snapshots.sortBy { it.updatedDate }
            return CountrySnapshot(countryName, snapshots)
        }
    }
}