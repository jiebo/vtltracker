package com.tijiebo.covidtracker.ui.repo

import com.tijiebo.covidtracker.core.network.ApiService
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot
import com.tijiebo.covidtracker.core.network.model.CountrySnapshotResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CovidTrackerRepo(private val api: ApiService) {

    private val vtlCountryCodes = arrayOf(
        "sg",
        "it",
        "fr",
        "au",
        "ca",
        "dk",
        "fi",
        "de",
//        "in",
//        "id",
//        "my",
//        "nl",
//        "kr",
//        "es",
//        "se",
//        "ch",
//        "bn"
    )

    fun getSnapshot(): Single<MutableList<CountrySnapshot>> {
        val observables = mutableListOf<Observable<List<CountrySnapshotResponse>>>()
        for (code in vtlCountryCodes) {
            observables.add(api.getCountryLatestSnapshot(code))
        }

        return Observable.merge(observables)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { CountrySnapshot.fromResponse(it) }
            .toList()
    }

    // TODO: Change to load from Local
    fun getCountryDate(code: String): Single<CountrySnapshot?> {
        return api.getCountryLatestSnapshot(code)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { CountrySnapshot.fromResponse(it) }
            .toList()
            .map { it.firstOrNull() }
    }
}