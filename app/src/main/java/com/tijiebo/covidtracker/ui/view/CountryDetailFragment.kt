package com.tijiebo.covidtracker.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.tijiebo.covidtracker.core.util.formatString
import com.tijiebo.covidtracker.databinding.CountryDetailFragmentBinding
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CountryDetailFragment : Fragment() {

    private val viewModel by sharedViewModel<DashboardViewModel>()

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
            historyAdapter?.setItems(it.daily)
        })
    }

    private fun initialiseViews() {
        binding.back.setOnClickListener { activity?.onBackPressed() }
        historyAdapter = CountryHistoryItemAdapter(mutableListOf())
        binding.detailHistoryRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            LinearSnapHelper().attachToRecyclerView(this)
        }
    }

    companion object {
        const val TAG = "CountryDetailFragment"
        fun newInstance() = CountryDetailFragment()
    }
}