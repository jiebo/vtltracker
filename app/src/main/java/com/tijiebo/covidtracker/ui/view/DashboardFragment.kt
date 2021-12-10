package com.tijiebo.covidtracker.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tijiebo.covidtracker.NavigationController
import com.tijiebo.covidtracker.R
import com.tijiebo.covidtracker.core.util.setVisible
import com.tijiebo.covidtracker.core.util.to2dp
import com.tijiebo.covidtracker.databinding.DashboardFragmentBinding
import com.tijiebo.covidtracker.ui.model.DashboardData
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    private val viewModel by sharedViewModel<DashboardViewModel>()
    private val disposables = CompositeDisposable()

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
        vtlAdapter = VtlCountryAdapter(mutableListOf(), viewModel)
        binding.vtlRecyclerView.apply {
            adapter = vtlAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observeVm() {
        viewModel.dashboardState.observe(viewLifecycleOwner, {
            when (it) {
                is DashboardViewModel.UiState.Initial -> {
                    binding.vtlGroup.visibility = View.GONE
                }
                is DashboardViewModel.UiState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is DashboardViewModel.UiState.Success -> {
                    setSuccessState(it.data)
                }
            }
            binding.primaryCountryCardGroup.visibility =
                if (it is DashboardViewModel.UiState.Success) View.VISIBLE else View.INVISIBLE
            binding.progress.setVisible(it is DashboardViewModel.UiState.Loading)
            binding.vtlGroup.setVisible(it is DashboardViewModel.UiState.Success)
        })
        disposables.addAll(
            viewModel.displayIgrInfo.subscribe { displayIgrInfo() },
            viewModel.displayCountryDetails.subscribe {
                viewModel.getCountryDetail(it)
                navigationController?.navigateToDetailsPage(it)
            }
        )
    }

    private fun setSuccessState(dashboardData: DashboardData) {
        binding.apply {
            primaryCountryName.text = dashboardData.primaryCountry.countryName
            primaryCountryCases.text = dashboardData.primaryCountry.latestConfirmed.toString()
            primaryCountryDeaths.text = dashboardData.primaryCountry.latestDeaths.toString()
            primaryCountryIGR.text = dashboardData.primaryCountry.infectionRate.to2dp()
        }
        vtlAdapter?.setItems(dashboardData.vtlCountries)
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