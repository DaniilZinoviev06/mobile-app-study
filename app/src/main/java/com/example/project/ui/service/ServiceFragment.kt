package com.example.project.ui.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.Service
import com.example.project.ServiceAdapter
import com.example.project.databinding.FragmentServiceBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ServiceFragment : Fragment() {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServiceViewModel by viewModels()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        loadData()
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(
            onItemClick = { service ->
                showServiceDetails(service)
            },
            onAddToCart = { service ->
                addToCart(service)
            }
        )

        binding.servicesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.services.collect { services ->
                    serviceAdapter.submitList(services)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    message?.let { errorMessage ->
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                        viewModel.errorMessageShown()
                    }
                }
            }
        }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadServices()
    }

    private fun showServiceDetails(service: Service) {
    }

    private fun addToCart(service: Service) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addToCart(service.id)
            Snackbar.make(
                binding.root,
                "${service.title} добавлен в заказ",
                Snackbar.LENGTH_SHORT
            )
                .setAction("Отмена") { viewModel.removeFromCart(service.id) }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}