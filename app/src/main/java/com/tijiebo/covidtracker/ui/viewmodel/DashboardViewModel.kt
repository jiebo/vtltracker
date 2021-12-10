package com.tijiebo.covidtracker.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot
import com.tijiebo.covidtracker.ui.model.DashboardData
import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class DashboardViewModel(private val repo: CovidTrackerRepo) : ViewModel() {

    private val disposable = CompositeDisposable()

    val dashboardState: MutableLiveData<UiState> = MutableLiveData(UiState.Initial)
    val countryDetails: MutableLiveData<CountrySnapshot> = MutableLiveData()
    val displayIgrInfo: PublishSubject<Boolean> = PublishSubject.create()
    val displayCountryDetails: PublishSubject<String> =
        PublishSubject.create()

    fun fetchAll() {
        disposable.add(
            repo.getSnapshot()
                .map {
                    DashboardData.fromCountrySnapshots(it)
                }
                .doOnSubscribe { dashboardState.postValue(UiState.Loading) }
                .subscribe({
                    dashboardState.postValue(UiState.Success(it))
                }, {
                    println("Inside ${it.localizedMessage}")
                })
        )
    }

    fun getCountryDetail(country: String) {
        disposable.add(
            repo.getCountryDate(country)
                .subscribe({
                    countryDetails.postValue(it)
                }, {

                })
        )
    }

    fun navigateToDetailsPage(country: DashboardData.CountrySummary) {
        displayCountryDetails.onNext(country.countryName)
    }

    fun displayIgrPopup() {
        displayIgrInfo.onNext(true)
    }

    override fun onCleared() {
        disposable.clear()
        displayIgrInfo.onComplete()
        displayCountryDetails.onComplete()
        super.onCleared()
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object Error : UiState()
        class Success(val data: DashboardData) : UiState()
    }
}