package com.olayg.onlykats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olayg.onlykats.databinding.ItemStatBinding

class StatAdapter(
    private val statList: MutableList<Pair<String, Int>> = mutableListOf()
) : RecyclerView.Adapter<StatAdapter.StatViewHolder>() {

    class StatViewHolder(
        private val binding: ItemStatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun loadStat(stat: Pair<String, Int>) = with(binding) {
            tvStatName.text = stat.first
            tvStatValue.text = stat.second.toString()
        }

        companion object {
            fun getInstance(parent: ViewGroup): StatViewHolder {
                val binding = ItemStatBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return StatViewHolder(binding)
            }
        }

    }

    fun addStats(stats: List<Pair<String,Int>>) {
        statList.addAll(stats)
        notifyItemRangeInserted(0,stats.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder =
        StatViewHolder.getInstance(parent)


    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.loadStat(statList[position])
    }
    override fun getItemCount(): Int = statList.size
}