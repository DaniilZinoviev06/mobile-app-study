package com.example.project.ui.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.Service
import com.example.project.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: ServiceRepository
) : ViewModel() {

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.initializeSampleData()
            loadServices()
        }
    }

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadServices() {
        viewModelScope.launch {
            try {
                repository.syncServices()
                repository.getLocalServices().let { services ->
                    _services.value = services
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки услуг: ${e.localizedMessage}"
            }
        }
    }

    fun addToCart(serviceId: String) {
        viewModelScope.launch {
        }
    }

    fun removeFromCart(serviceId: String) {
        viewModelScope.launch {
        }
    }

    fun errorMessageShown() {
        _errorMessage.value = null
    }
}