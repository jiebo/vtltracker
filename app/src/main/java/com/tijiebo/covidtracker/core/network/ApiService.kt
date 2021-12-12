package com.tijiebo.covidtracker.core.network

import com.tijiebo.covidtracker.core.network.model.CountrySnapshotResponse
import com.tijiebo.covidtracker.core.util.formatForNetwork
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface ApiService {

    @GET("total/country/{countryId}")
    fun getCountryLatestSnapshot(
        @Path("countryId") countryId: String,
        @Query("from", encoded = true) fromDate: String =
            LocalDate.now().minusDays(16).formatForNetwork(),
        @Query("to", encoded = true) toDate: String =
            LocalDate.now().formatForNetwork()
    ): Observable<List<CountrySnapshotResponse>>
}