package com.tijiebo.covidtracker.ui.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tijiebo.covidtracker.R
import com.tijiebo.covidtracker.core.util.formatString
import com.tijiebo.covidtracker.core.util.to3dp
import com.tijiebo.covidtracker.databinding.CountryDetailFragmentBinding
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.viewModel

class CountryDetailFragment : Fragment() {

    private val viewModel by viewModel<DashboardViewModel>()
    private val disposables = CompositeDisposable()

    private var historyAdapter: CountryHistoryItemAdapter? = null
    private var _binding: CountryDetailFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CountryDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseViews()
        viewModel.countryDetails.observe(viewLifecycleOwner, {
            binding.countryTitle.text = it.countryName
            binding.detailCases.text = it.cumulativeCases.formatString()
            binding.detailDeaths.text = it.cumulativeDeaths.formatString()
            binding.detailIgr.text = it.igr.to3dp()
            historyAdapter?.setItems(it.daily)
        })
        disposables.add(
            viewModel.displayError.subscribe { resId ->
                displayErrorPage(resId)
                binding.contentGroup.visibility = View.INVISIBLE
            }
        )
        arguments?.getString(COUNTRY_ID)?.let { viewModel.getCountryDetail(it) }
    }

    private fun displayErrorPage(resId: Int) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setMessage(resId)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    activity?.onBackPressed()
                }
                .show()
        }
    }

    private fun initialiseViews() {
        binding.back.setOnClickListener { activity?.onBackPressed() }
        historyAdapter = CountryHistoryItemAdapter(mutableListOf())
        binding.detailHistoryRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    companion object {
        const val TAG = "CountryDetailFragment"
        const val COUNTRY_ID = "country_id"
        fun newInstance(countryId: String) = CountryDetailFragment().apply {
            arguments = Bundle().apply {
                putString(COUNTRY_ID, countryId)
            }
        }
    }
}