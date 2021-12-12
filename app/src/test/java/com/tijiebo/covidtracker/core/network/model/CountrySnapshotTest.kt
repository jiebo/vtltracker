package com.tijiebo.covidtracker.core.network.model

import com.tijiebo.covidtracker.core.util.to3dp
import org.junit.Test
import java.util.*

class CountrySnapshotTest {

    @Test
    fun `Test toCountrySummary`() {
        val countrySummary = getMockSGSnapshot().toCountrySummary()
        assert(countrySummary.countryName == "SG")
        assert(countrySummary.infectionRate.to3dp() == "0.659")
        assert(countrySummary.latestConfirmed == 272433 - 271979)
        assert(countrySummary.latestDeaths == 783 - 779)
    }

    @Test
    fun `Test toCountryDetails`() {
        val countryDetails = getMockSGSnapshot().toCountryDetails()
        assert(countryDetails.countryName == "SG")
        assert(countryDetails.cumulativeCases == 272433)
        assert(countryDetails.cumulativeDeaths == 783)
        assert(countryDetails.igr.to3dp() == "0.659")
        assert(countryDetails.daily[0].cases == 260946 - 259191)
        assert(countryDetails.daily[7].deaths == 746 - 744)
    }

    @Test
    fun `Test fromResponse`() {
        val countrySnapshot = CountrySnapshot.fromResponse(getMockResponse())
        assert(countrySnapshot.countryName == "SG")
        assert(countrySnapshot.daily.size == 15)
        assert(countrySnapshot.daily[0].confirmed == getMockResponse()[0].confirmed)
        assert(countrySnapshot.daily[7].deaths == getMockResponse()[7].deaths)
    }

    private fun getMockResponse(): List<CountrySnapshotResponse> {
        val today = Calendar.getInstance()
        today.time = Date()
        today.add(Calendar.DATE, -15)
        val responseList = mutableListOf<CountrySnapshotResponse>()
        val pairs = listOf(
            Pair(259191, 684), // Sun
            Pair(260946, 690), // Mon
            Pair(261682, 701),
            Pair(262776, 710),
            Pair(264007, 718),
            Pair(265323, 726),
            Pair(266415, 735),
            Pair(267172, 744), // Sun
            Pair(267913, 746), // Mon
            Pair(268452, 759),
            Pair(269873, 763),
            Pair(270588, 771),
            Pair(271297, 774),
            Pair(271979, 779),
            Pair(272433, 783), // Sun
        )
        for (pair in pairs) {
            responseList.add(
                CountrySnapshotResponse(
                    "SG",
                    pair.first,
                    pair.second,
                    today.apply { add(Calendar.DATE, 1) }.time
                )
            )
        }
        return responseList
    }

    private fun getMockSGSnapshot(): CountrySnapshot {
        val today = Calendar.getInstance()
        today.time = Date()
        today.add(Calendar.DATE, -15)
        val dailyList = mutableListOf<CountrySnapshot.Snapshot>()
        val pairs = listOf(
            Pair(259191, 684), // Sun
            Pair(260946, 690), // Mon
            Pair(261682, 701),
            Pair(262776, 710),
            Pair(264007, 718),
            Pair(265323, 726),
            Pair(266415, 735),
            Pair(267172, 744), // Sun
            Pair(267913, 746), // Mon
            Pair(268452, 759),
            Pair(269873, 763),
            Pair(270588, 771),
            Pair(271297, 774),
            Pair(271979, 779),
            Pair(272433, 783), // Sun
        )
        for (pair in pairs) {
            dailyList.add(
                CountrySnapshot.Snapshot(
                    pair.first,
                    pair.second,
                    today.apply { add(Calendar.DATE, 1) }.time
                )
            )
        }
        return CountrySnapshot("SG", dailyList)
    }
}