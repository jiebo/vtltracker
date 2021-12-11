package com.tijiebo.covidtracker.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tijiebo.covidtracker.NavigationController
import com.tijiebo.covidtracker.R
import com.tijiebo.covidtracker.core.cache.CovidSharedPreferences
import com.tijiebo.covidtracker.core.util.formatString
import com.tijiebo.covidtracker.core.util.setVisible
import com.tijiebo.covidtracker.core.util.to2dp
import com.tijiebo.covidtracker.databinding.DashboardFragmentBinding
import com.tijiebo.covidtracker.ui.model.DashboardData
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel.UiState
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardFragment : Fragment() {

    private val clearPrefs: CovidSharedPreferences by inject()

    private val viewModel by sharedViewModel<DashboardViewModel>()
    private val disposables = CompositeDisposable()

    private var snackbar: Snackbar? = null
    private var navigationController: NavigationController? = null
    private var vtlAdapter: VtlCountryAdapter? = null
    private var _binding: DashboardFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseViews()
        observeVm()
        viewModel.fetchAll()
    }

    private fun initialiseViews() {
        binding.primaryCountryIGRInfo.setOnClickListener { viewModel.displayIgrPopup() }
        binding.primaryCountryIGRInfo.setOnLongClickListener {
            clearPrefs.clearCache()
            true
        }
        binding.refresh.setOnClickListener { viewModel.fetchAll() }
        vtlAdapter = VtlCountryAdapter(mutableListOf(), viewModel)
        binding.vtlRecyclerView.apply {
            adapter = vtlAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        binding.swipeRefresh.setOnRefreshListener { viewModel.fetchAll(true) }
    }

    private fun observeVm() {
        viewModel.dashboardState.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Initial -> binding.vtlGroup.visibility = View.GONE
                is UiState.Loading -> binding.progress.visibility = View.VISIBLE
                is UiState.Latest -> setLatestState(it.data)
                is UiState.Cached -> setCachedState(it.data)
                is UiState.Error -> if (it.withData) setErrorState()
            }
            binding.progress.setVisible(it is UiState.Loading)
            binding.tryAgainGroup.setVisible(it is UiState.Error && !it.withData)
        })
        disposables.addAll(
            viewModel.displayIgrInfo.subscribe { displayIgrInfo() },
            viewModel.displayCountryDetails.subscribe({
                viewModel.getCountryDetail(it)
                navigationController?.navigateToDetailsPage(it)
            }, {

            })
        )
    }

    private fun setLatestState(dashboardData: DashboardData) {
        snackbar?.let { if (it.isShown) it.dismiss() }
        snackbar = null
        binding.swipeRefresh.apply {
            if (this.isRefreshing) this.isRefreshing = false
        }
        displayDashboardData(dashboardData)
    }

    private fun setCachedState(dashboardData: DashboardData) {
        snackbar =
            Snackbar.make(binding.root, R.string.cache_disclaimer, Snackbar.LENGTH_LONG).apply {
                show()
            }
        displayDashboardData(dashboardData)
    }

    private fun displayDashboardData(dashboardData: DashboardData) {
        binding.apply {
            primaryCountryName.text = dashboardData.primaryCountry.countryName
            primaryCountryCases.text = dashboardData.primaryCountry.latestConfirmed.formatString()
            primaryCountryDeaths.text = dashboardData.primaryCountry.latestDeaths.formatString()
            primaryCountryIGR.text = dashboardData.primaryCountry.infectionRate.to2dp()
            primaryCountryCardGroup.visibility = View.VISIBLE
            vtlGroup.visibility = View.VISIBLE
        }
        vtlAdapter?.setItems(dashboardData.vtlCountries)
    }

    private fun setErrorState() {
        snackbar =
            Snackbar.make(binding.root, R.string.network_failed, Snackbar.LENGTH_INDEFINITE)
                .apply {
                    setAction(R.string.try_again) {
                        viewModel.fetchAll()
                    }
                    show()
                }

    }

    private fun displayIgrInfo() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.igr_disclaimer_title)
                .setMessage(R.string.igr_disclaimer_message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationController = context as? NavigationController
    }

    companion object {
        const val TAG = "DashboardFragment"
        fun newInstance() = DashboardFragment()
    }
}