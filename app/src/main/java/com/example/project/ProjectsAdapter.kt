package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project.databinding.ItemProjectBinding

class ProjectsAdapter : ListAdapter<ConstructionProject, ProjectsAdapter.ProjectViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProjectViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(project: ConstructionProject) {
            with(binding) {
                projectImage.setImageResource(project.imageRes)
                projectTitle.text = project.title
                projectAddress.text = project.address
                progressBar.progress = project.progress
                progressText.text = "${project.progress}% завершено"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ConstructionProject>() {
        override fun areItemsTheSame(oldItem: ConstructionProject, newItem: ConstructionProject) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ConstructionProject, newItem: ConstructionProject) =
            oldItem == newItem
    }
}