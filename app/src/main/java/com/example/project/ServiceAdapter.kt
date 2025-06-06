package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.project.databinding.ItemServiceListBinding

class ServiceAdapter(
    private val onItemClick: (Service) -> Unit,
    private val onAddToCart: (Service) -> Unit,
    private val showRemoveButton: Boolean = false
) : ListAdapter<Service, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    inner class ServiceViewHolder(
        private val binding: ItemServiceListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            with(binding) {
                serviceImage.setImageDrawable(null)

                serviceTitle.text = service.title
                servicePrice.text = "%.2f ₽".format(service.price)
                serviceDuration.text = service.durationDays?.let { "$it дней" } ?: ""
                serviceArea.text = service.area?.let { "${it} м²" } ?: ""

                addToCartButton.text = if (showRemoveButton) "Убрать из корзины" else "Добавить в корзину"

                Glide.with(root.context)
                    .load(service.imageUrl)
                    .placeholder(R.drawable.ic_construction)
                    .error(R.drawable.ic_construction)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(serviceImage)

                root.setOnClickListener { onItemClick(service) }
                addToCartButton.setOnClickListener { onAddToCart(service) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ServiceDiffCallback : DiffUtil.ItemCallback<Service>() {
    override fun areItemsTheSame(oldItem: Service, newItem: Service) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Service, newItem: Service) = oldItem == newItem
}