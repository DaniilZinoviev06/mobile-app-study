package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project.databinding.ItemServiceBinding

class ServicesAdapter : ListAdapter<ConstructionService, ServicesAdapter.ServiceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(private val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: ConstructionService) {
            binding.apply {
                serviceIcon.setImageResource(service.iconRes)
                serviceName.text = service.name
                serviceDescription.text = service.description
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ConstructionService>() {
        override fun areItemsTheSame(oldItem: ConstructionService, newItem: ConstructionService) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ConstructionService, newItem: ConstructionService) =
            oldItem == newItem
    }
}