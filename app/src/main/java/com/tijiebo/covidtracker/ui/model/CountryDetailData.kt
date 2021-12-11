package com.tijiebo.covidtracker.ui.model

import java.util.*

data class CountryDetailData(
    val countryName: String,
    val cumulativeCases: Int,
    val cumulativeDeaths: Int,
    val daily: List<DailyDeltas>
) {
    data class DailyDeltas(
        val cases: Int,
        val deaths: Int,
        val date: Date
    )
}
