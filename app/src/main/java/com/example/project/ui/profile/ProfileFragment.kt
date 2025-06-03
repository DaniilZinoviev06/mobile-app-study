package com.example.project.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project.R
import com.example.project.Service
import com.example.project.ServiceAdapter
import com.example.project.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var serviceAdapter: ServiceAdapter

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.onPhotoPicked(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(
            onItemClick = { service ->
                showServiceDetails(service)
            },
            onAddToCart = { service ->
                viewModel.removeServiceFromCart(service)
            },
            showRemoveButton = true
        )

        binding.servicesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
            setHasFixedSize(true)
        }
    }


    private fun showServiceDetails(service: Service) {

    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest { user ->
                user?.let {
                    binding.emailTextView.text = it.email
                    binding.nameEditText.setText(it.displayName ?: "")

                    it.photoUrl?.let { url ->
                        Glide.with(this@ProfileFragment)
                            .load(url)
                            .error(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(binding.profileImageView)
                    } ?: binding.profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userServices.collectLatest { services ->
                serviceAdapter.submitList(services)
                binding.emptyState.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
                binding.progressBarRoot.visibility = View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loadingState.collect { isLoading ->
                if (isLoading) {
                    binding.progressBarRoot.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.errorState.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorState()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.pickPhotoEvent.collect { event ->
                event?.let {
                    pickImage.launch("image/*")
                    viewModel.clearPickPhotoEvent()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString()
            if (newName.isNotBlank()) {
                viewModel.updateDisplayName(newName)
            } else {
                Toast.makeText(requireContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }

        binding.changePhotoButton.setOnClickListener {
            viewModel.pickProfilePhoto()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}