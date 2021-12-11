package com.tijiebo.covidtracker.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tijiebo.covidtracker.core.util.formatString
import com.tijiebo.covidtracker.core.util.to2dp
import com.tijiebo.covidtracker.databinding.VtlItemBinding
import com.tijiebo.covidtracker.ui.model.DashboardData
import com.tijiebo.covidtracker.ui.viewmodel.DashboardViewModel

class VtlCountryAdapter(
    private val vtlCountries: MutableList<DashboardData.CountrySummary>,
    private val viewModel: DashboardViewModel
) : RecyclerView.Adapter<VtlCountryAdapter.VtlCountryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VtlCountryViewHolder {
        val binding = VtlItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VtlCountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VtlCountryViewHolder, position: Int) {
        holder.bind(vtlCountries[position])
    }

    override fun getItemCount() = vtlCountries.size

    fun setItems(newList: List<DashboardData.CountrySummary>) {
        vtlCountries.clear()
        vtlCountries.addAll(newList)
        notifyDataSetChanged()
    }

    inner class VtlCountryViewHolder(private val binding: VtlItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(countrySummary: DashboardData.CountrySummary) {
            binding.vtlCountryName.text = countrySummary.countryName
            binding.vtlCases.text = countrySummary.latestConfirmed.formatString()
            binding.vtlDeaths.text = countrySummary.latestDeaths.formatString()
            binding.vtlIGR.text = countrySummary.infectionRate.to2dp()
            itemView.setOnClickListener {
                viewModel.navigateToDetailsPage(countrySummary)
            }
        }
    }
}