package com.tijiebo.covidtracker.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tijiebo.covidtracker.R
import com.tijiebo.covidtracker.core.util.formatString
import com.tijiebo.covidtracker.core.util.formatToCalendarDate
import com.tijiebo.covidtracker.databinding.HistoryItemBinding
import com.tijiebo.covidtracker.ui.model.CountryDetailData
import java.util.*

class CountryHistoryItemAdapter(
    private val list: MutableList<CountryDetailData.DailyDeltas>
) : RecyclerView.Adapter<CountryHistoryItemAdapter.CountryHistoryItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryHistoryItemViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryHistoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryHistoryItemViewHolder, position: Int) {
        holder.bind(list[list.size - position - 1]) // Reverse order
    }

    override fun getItemCount() = list.size

    fun setItems(newList: List<CountryDetailData.DailyDeltas>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class CountryHistoryItemViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(delta: CountryDetailData.DailyDeltas) {
            binding.date.text = delta.date.formatToCalendarDate()
            binding.cases.text = delta.cases.formatString()
            binding.deaths.text = delta.deaths.formatString()
            val calendar = Calendar.getInstance().apply {
                this.time = delta.date
            }
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                    )
                        R.color.colorSecondaryDark
                    else R.color.white
                )
            )
        }
    }
}