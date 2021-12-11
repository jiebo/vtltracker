package com.tijiebo.covidtracker.ui.repo

import com.tijiebo.covidtracker.R
import com.tijiebo.covidtracker.core.cache.CovidSharedPreferences
import com.tijiebo.covidtracker.core.network.ApiService
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot
import com.tijiebo.covidtracker.core.network.model.CountrySnapshotResponse
import com.tijiebo.covidtracker.ui.model.CountryDetailData
import com.tijiebo.covidtracker.ui.model.GeneralServiceException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CovidTrackerRepo(
    private val api: ApiService,
    private val cache: CovidSharedPreferences
) {

    private val vtlCountryCodes = arrayOf(
        "sg",
        "it",
        "fr",
        "au",
        "ca",
        "dk",
        "fi",
        "de",
        "ch",
//        "in",
//        "id",
//        "my",
//        "nl",
//        "kr",
//        "es",
//        "se",
//        "bn"
    )

    fun getData(forceRefresh: Boolean = false): Observable<RepoResult> {
        return Observable.merge(
            if (forceRefresh) Observable.just(RepoResult.NoOp) else getCachedData(),
            getNetworkData()
        )
    }

    private fun getNetworkData(): Observable<RepoResult.Latest> {
        val observables = mutableListOf<Observable<List<CountrySnapshotResponse>>>()
        for (code in vtlCountryCodes) {
            observables.add(api.getCountryLatestSnapshot(code))
        }

        return Observable.merge(observables)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { CountrySnapshot.fromResponse(it) }
            .toList()
            .doAfterSuccess { cache.saveData(it) }
            .map { RepoResult.Latest(it) }
            .toObservable()
    }

    private fun getCachedData(): Observable<RepoResult> {
        val cacheData = cache.getAllData()
        return Observable.just(
            if (cacheData.isEmpty()) RepoResult.EmptyCache
            else RepoResult.Cached(cacheData)
        )
    }

    fun getCountryDate(code: String): Single<CountryDetailData> {
        return cache.getCountryData(code)?.let {
            Single.just(it.toCountryDetails())
        } ?: throw GeneralServiceException(R.string.country_detail_not_available)
    }

    sealed class RepoResult {
        class Cached(val data: List<CountrySnapshot>) : RepoResult()
        object EmptyCache : RepoResult()
        object NoOp : RepoResult()
        class Latest(val data: MutableList<CountrySnapshot>) : RepoResult()
    }
}