package com.tijiebo.covidtracker.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot
import com.tijiebo.covidtracker.ui.model.CountryDetailData
import com.tijiebo.covidtracker.ui.model.GeneralServiceException
import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo
import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo.RepoResult
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel.UiState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repo: CovidTrackerRepo

    private lateinit var viewModel: DashboardViewModel
    private val events = mutableListOf<UiState>()
    private val observer = Observer<UiState> { events.add(it) }

    @Before
    fun setUp() {
        viewModel = Mockito.spy(DashboardViewModel(repo))
    }

    @Test
    fun `On Cold Cache, Network Success`() {
        viewModel.dashboardState.observeForever(observer)
        Mockito.`when`(repo.getData())
            .thenReturn(
                Observable.merge(
                    Observable.just(RepoResult.EmptyCache),
                    Observable.just(RepoResult.Latest(getExpectedResponseFromRepo()))
                )
            )
        viewModel.fetchAll()
        assert(events[0] is UiState.Initial)
        assert(events[1] is UiState.Loading)
        assert(events[2] is UiState.Loading)
        assert(events[3] is UiState.Latest)
    }

    @Test
    fun `On Hot Cache, Network Success`() {
        viewModel.dashboardState.observeForever(observer)
        Mockito.`when`(repo.getData())
            .thenReturn(
                Observable.merge(
                    Observable.just(RepoResult.Cached(getExpectedResponseFromRepo())),
                    Observable.just(RepoResult.Latest(getExpectedResponseFromRepo()))
                )
            )
        viewModel.fetchAll()
        assert(events[0] is UiState.Initial)
        assert(events[1] is UiState.Loading)
        assert(events[2] is UiState.Cached)
        assert(events[3] is UiState.Latest)
    }

    @Test
    fun `On Cold Cache, Network Error`() {
        viewModel.dashboardState.observeForever(observer)
        Mockito.`when`(repo.getData())
            .thenReturn(
                Observable.merge(
                    Observable.just(RepoResult.EmptyCache),
                    Observable.error(GeneralServiceException(0))
                )
            )
        viewModel.fetchAll()
        assert(events[0] is UiState.Initial)
        assert(events[1] is UiState.Loading)
        assert(events[2] is UiState.Loading)
        assert(events[3] is UiState.Error)
        assert((events[3] as? UiState.Error)?.withData == false)
    }

    @Test
    fun `On Hot Cache, Network Error`() {
        viewModel.dashboardState.observeForever(observer)
        Mockito.`when`(repo.getData())
            .thenReturn(
                Observable.merge(
                    Observable.just(RepoResult.Cached(getExpectedResponseFromRepo())),
                    Observable.error(GeneralServiceException(0))
                )
            )
        viewModel.fetchAll()
        assert(events[0] is UiState.Initial)
        assert(events[1] is UiState.Loading)
        assert(events[2] is UiState.Cached)
        assert(events[3] is UiState.Error)
        assert((events[3] as? UiState.Error)?.withData == true)
    }

    @Test
    fun `On getCountryDetail Success`() {
        Mockito.`when`(repo.getCountryData(Mockito.anyString()))
            .thenReturn(Single.just(getExpectedCountryDetailData()))
        viewModel.getCountryDetail(Mockito.anyString())
        assert(viewModel.countryDetails.value?.countryName == "Singapore")
    }

    @Test
    fun `On getCountryDetail Error`() {
        val testSub = TestObserver<Int>()
        Mockito.`when`(repo.getCountryData(Mockito.anyString()))
            .thenReturn(Single.error(GeneralServiceException(123)))
        viewModel.displayError.subscribe(testSub)
        viewModel.getCountryDetail(Mockito.anyString())
        testSub.assertValue(123)
    }

    private fun getExpectedResponseFromRepo(): MutableList<CountrySnapshot> {
        return mutableListOf(
            CountrySnapshot("Singapore", listOf()),
            CountrySnapshot("France", listOf())
        )
    }

    private fun getExpectedCountryDetailData(): CountryDetailData {
        return CountryDetailData(
            countryName = "Singapore",
            cumulativeCases = 0,
            cumulativeDeaths = 0,
            igr = 0.0,
            daily = emptyList()
        )
    }
}