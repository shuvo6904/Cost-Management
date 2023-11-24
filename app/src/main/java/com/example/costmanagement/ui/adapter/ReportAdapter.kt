package com.example.costmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.costmanagement.databinding.ItemReportBinding
import com.example.costmanagement.network.CostModel

class ReportAdapter :
    ListAdapter<CostModel, ReportAdapter.ReportAdapterViewHolder>(
        DIFF_CALLBACK
    ) {
    companion object {

        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<CostModel>() {
            override fun areItemsTheSame(
                oldItem: CostModel,
                newItem: CostModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CostModel,
                newItem: CostModel
            ): Boolean {
                return oldItem == newItem
            }
        }

    }

    class ReportAdapterViewHolder private constructor(
        private val binding: ItemReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CostModel) {
            binding.name.text = "Name: ${item.name}"
            binding.cost.text = "Cost: ${item.cost?.toInt()}"
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): ReportAdapterViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemReportBinding.inflate(layoutInflater, parent, false)
                return ReportAdapterViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportAdapterViewHolder {
        return ReportAdapterViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReportAdapterViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}
