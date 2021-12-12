package com.tijiebo.covidtracker.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tijiebo.covidtracker.R
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot
import com.tijiebo.covidtracker.ui.model.CountryDetailData
import com.tijiebo.covidtracker.ui.model.DashboardData
import com.tijiebo.covidtracker.ui.model.GeneralServiceException
import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo
import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo.RepoResult.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class DashboardViewModel(private val repo: CovidTrackerRepo) : ViewModel() {

    private val disposable = CompositeDisposable()

    val dashboardState: MutableLiveData<UiState> = MutableLiveData(UiState.Initial)
    val countryDetails: MutableLiveData<CountryDetailData> = MutableLiveData()
    val displayError: PublishSubject<Int> = PublishSubject.create()
    val displayIgrInfo: PublishSubject<Boolean> = PublishSubject.create()
    val displayCountryDetails: PublishSubject<String> = PublishSubject.create()

    fun fetchAll(forceRefresh: Boolean = false) {
        disposable.add(
            repo.getData(forceRefresh)
                .doOnSubscribe { dashboardState.postValue(UiState.Loading) }
                .subscribe({
                    dashboardState.postValue(
                        when (it) {
                            is Cached -> UiState.Cached(
                                it.data?.let { data -> DashboardData.fromCountrySnapshots(data) }
                            )
                            is Latest -> UiState.Latest(DashboardData.fromCountrySnapshots(it.data))
                            else -> UiState.Loading
                        }
                    )
                }, {
                    dashboardState.postValue(
                        UiState.Error(
                            dashboardState.value is UiState.Cached ||
                                    dashboardState.value is UiState.Latest
                        )
                    )
                })
        )
    }

    fun getCountryDetail(country: String) {
        disposable.add(
            repo.getCountryDate(country)
                .subscribe({
                    countryDetails.postValue(it)
                }, {
                    displayError.onNext(
                        (it as? GeneralServiceException)?.errorResId
                            ?: R.string.generic_error_message
                    )
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
        displayError.onComplete()
        displayIgrInfo.onComplete()
        displayCountryDetails.onComplete()
        super.onCleared()
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object NoOp : UiState()
        class Error(val withData: Boolean) : UiState()
        class Cached(val data: DashboardData?) : UiState()
        class Latest(val data: DashboardData) : UiState()
    }
}