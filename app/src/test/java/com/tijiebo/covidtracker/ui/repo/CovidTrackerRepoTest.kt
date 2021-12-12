package com.tijiebo.covidtracker.ui.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tijiebo.covidtracker.core.cache.CacheService
import com.tijiebo.covidtracker.core.network.ApiService
import com.tijiebo.covidtracker.core.network.model.CountrySnapshot
import com.tijiebo.covidtracker.core.network.model.CountrySnapshotResponse
import com.tijiebo.covidtracker.core.util.formatToCalendarDate
import com.tijiebo.covidtracker.ui.repo.CovidTrackerRepo.RepoResult
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.functions.Predicate
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CovidTrackerRepoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var api: ApiService

    @Mock
    lateinit var cache: CacheService

    private lateinit var repo: CovidTrackerRepo

    @Before
    fun setUp() {
        repo = CovidTrackerRepo(api, cache)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setErrorHandler { Schedulers.trampoline() }
    }

    @Test
    fun `On Network Returns Success`() {
        Mockito.`when`(api.getCountryLatestSnapshot("SG"))
            .thenReturn(Observable.just(getMockSGResponse()))
        Mockito.`when`(api.getCountryLatestSnapshot("FR"))
            .thenReturn(Observable.just(getMockFRResponse()))
        repo.getNetworkData(arrayOf("SG", "FR"))
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { it is RepoResult.Latest }
            .assertValue { (it as RepoResult.Latest).data.size == 2 }
        verify(cache).saveData(Mockito.anyList())
    }

    @Test
    fun `On Network Returns Error`() {
        Mockito.`when`(api.getCountryLatestSnapshot("SG"))
            .thenReturn(Observable.error(Exception()))
        Mockito.`when`(api.getCountryLatestSnapshot("FR"))
            .thenReturn(Observable.just(getMockFRResponse()))
        repo.getNetworkData(arrayOf("SG", "FR"))
            .test()
            .assertNotComplete()
        verify(cache, never()).saveData(Mockito.anyList())
    }

    @Test
    fun `Verify response parsing`() {
        Mockito.`when`(api.getCountryLatestSnapshot("SG"))
            .thenReturn(Observable.just(getMockSGResponse()))
        Mockito.`when`(api.getCountryLatestSnapshot("FR"))
            .thenReturn(Observable.just(getMockFRResponse()))
        repo.getNetworkData(arrayOf("SG", "FR"))
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { it is RepoResult.Latest }
            .assertValue {
                assert(it is RepoResult.Latest)
                val data = (it as RepoResult.Latest).data
                assert(data.firstOrNull() { sg -> sg.countryName == "SG" } != null)
                val sg = data.first { SG -> SG.countryName == "SG" }
                assert(sg.daily.size == 5)
                assert(sg.daily[4].confirmed == 42312)
                return@assertValue true
            }
        verify(cache).saveData(Mockito.anyList())
    }

    @Test
    fun `On Hot Cache`() {
        Mockito.`when`(cache.getAllData())
            .thenReturn(listOf(CountrySnapshot("", listOf())))
        repo.getCachedData()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { it is RepoResult.Cached }
            .assertValue { (it as RepoResult.Cached).data?.size == 1 }
    }

    @Test
    fun `On Cold Cache`() {
        Mockito.`when`(cache.getAllData())
            .thenReturn(emptyList())
        repo.getCachedData()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { it is RepoResult.EmptyCache }
    }

    @Test
    fun `On Skip Cache`() {
        repo.getCachedData(skipCache = true)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { it is RepoResult.Cached }
            .assertValue { (it as RepoResult.Cached).data == null }
    }

    private fun getMockSGResponse(): List<CountrySnapshotResponse> {
        val today = Calendar.getInstance()
        today.time = Date()
        return listOf(
            CountrySnapshotResponse("SG", 42312, 424, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("SG", 42313, 425, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("SG", 42314, 426, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("SG", 42315, 427, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("SG", 42316, 428, today.apply { add(Calendar.DATE, -1) }.time),
        )
    }

    private fun getMockFRResponse(): List<CountrySnapshotResponse> {
        val today = Calendar.getInstance()
        today.time = Date()
        return listOf(
            CountrySnapshotResponse("FR", 4212, 44, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("FR", 4213, 45, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("FR", 4214, 46, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("FR", 4215, 47, today.apply { add(Calendar.DATE, -1) }.time),
            CountrySnapshotResponse("FR", 4216, 48, today.apply { add(Calendar.DATE, -1) }.time),

            )
    }
}