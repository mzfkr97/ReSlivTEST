package com.example.reslivtest.ui.city.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reslivtest.databinding.ItemCityBinding
import com.example.reslivtest.util.database.CityData

class CityAdapter(
        private val clickListener: CityItemClickListener
) : ListAdapter<
        CityData,
        CityAdapter.ViewHolder>(TaxiDiffCallBack()) {

    class CityItemClickListener(val clickListener: (item: CityData?) -> Unit) {
        fun onCityItemClick(item: CityData?) {
            clickListener(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city: CityData = getItem(position)
        holder.bind(city, clickListener)
    }

    class ViewHolder private constructor(private val binding: ItemCityBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(city: CityData, clickListener: CityItemClickListener) {
            binding.cityData = city
            binding.cityItemClickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val taxiBinding = ItemCityBinding.inflate(inflater, parent, false)
                return ViewHolder(taxiBinding)
            }
        }
    }

    class TaxiDiffCallBack : DiffUtil.ItemCallback<CityData>() {
        override fun areItemsTheSame(oldItem: CityData, newItem: CityData): Boolean {
            return oldItem.cityName == newItem.cityName
        }

        override fun areContentsTheSame(oldItem: CityData, newItem: CityData): Boolean {
            return oldItem == newItem
        }
    }
}