package com.example.project.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.databinding.FragmentHomeBinding
import com.example.project.ProjectsAdapter
import com.example.project.ServicesAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()
        setupHeader()
    }

    private fun setupRecyclerViews() {
        // Настройка RecyclerView для проектов
        binding.projectsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ProjectsAdapter()
            setHasFixedSize(true)
        }

        // Настройка RecyclerView для услуг
        binding.servicesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = ServicesAdapter()
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            (binding.projectsRecyclerView.adapter as ProjectsAdapter).submitList(projects)
        }

        viewModel.services.observe(viewLifecycleOwner) { services ->
            (binding.servicesRecyclerView.adapter as ServicesAdapter).submitList(services)
        }
    }

    private fun setupHeader() {
        binding.headerTitle.text = getString(R.string.company_name)
        binding.promotionTitle.text = getString(R.string.current_projects)
        binding.promotionText.text = getString(R.string.company_description)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}