package com.example.project.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.AppDatabase
import com.example.project.Service
import com.example.project.User
import com.example.project.UserServiceCrossRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val db: AppDatabase,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _userServices = MutableStateFlow<List<Service>>(emptyList())
    val userServices: StateFlow<List<Service>> = _userServices.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val _pickPhotoEvent = MutableStateFlow<Unit?>(null)
    val pickPhotoEvent: StateFlow<Unit?> = _pickPhotoEvent.asStateFlow()

    init {
        loadCurrentUserData()
    }

    fun clearErrorState() {
        _errorState.value = null
    }

    fun clearPickPhotoEvent() {
        _pickPhotoEvent.value = null
    }

    private fun loadCurrentUserData() {
        val currentUser = auth.currentUser ?: run {
            _errorState.value = "Пользователь не авторизован"
            return
        }
        loadUserData(currentUser.uid)
    }

    fun loadUserData(userId: String) {
        if (userId.isBlank()) {
            _errorState.value = "некорректный ID"
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            try {
                // Load user data
                val user = db.userDao().getUser(userId) ?: User(
                    uid = userId,
                    email = auth.currentUser?.email ?: ""
                ).also {
                    db.userDao().insertUser(it)
                }

                auth.currentUser?.let { firebaseUser ->
                    user.displayName = firebaseUser.displayName
                    user.photoUrl = firebaseUser.photoUrl?.toString()
                }

                _user.value = user

                launch {
                    db.userServiceCrossRefDao().getServicesForUser(userId).collect { services ->
                        _userServices.value = services
                        _loadingState.value = false
                    }
                }
            } catch (e: Exception) {
                _errorState.value = "Проблема с загрузкой: ${e.message}"
                _loadingState.value = false
            }
        }
    }

    fun updateDisplayName(newName: String) {
        val currentUser = _user.value ?: return

        viewModelScope.launch {
            _loadingState.value = true
            try {
                currentUser.displayName = newName
                db.userDao().insertUser(currentUser)

                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()
                )?.await()

                _user.value = currentUser
            } catch (e: Exception) {
                _errorState.value = "Не удалось обновить: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun removeServiceFromCart(service: Service) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                db.userServiceCrossRefDao().deleteByUserAndService(userId, service.id)
                loadUserData(userId)
            } catch (e: Exception) {
                _errorState.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun pickProfilePhoto() {
        _pickPhotoEvent.value = Unit
    }

    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val storageRef = storage.reference
                    .child("profile_images")
                    .child("$userId.jpg")

                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                _user.value?.let { user ->
                    user.photoUrl = downloadUrl
                    db.userDao().insertUser(user)
                    _user.value = user
                }

                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(downloadUrl))
                        .build()
                )?.await()

            } catch (e: Exception) {
                _errorState.value = "Не удалось добавить фото: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun addServiceToCart(service: Service) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val crossRef = UserServiceCrossRef(userId, service.id)
                db.userServiceCrossRefDao().insertUserServiceCrossRef(crossRef)
            } catch (e: Exception) {
                _errorState.value = "Ошибка добавления сервиса: ${e.message}"
            }
        }
    }
}