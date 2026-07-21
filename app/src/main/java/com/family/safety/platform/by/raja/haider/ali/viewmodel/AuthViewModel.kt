package com.family.safety.platform.by.raja.haider.ali.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.safety.platform.by.raja.haider.ali.model.User
import com.family.safety.platform.by.raja.haider.ali.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState

    private val _registerState = MutableLiveData<AuthState>()
    val registerState: LiveData<AuthState> = _registerState

    private val _forgotState = MutableLiveData<AuthState>()
    val forgotState: LiveData<AuthState> = _forgotState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun login(email: String, password: String) {
        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            repo.login(email, password).fold(
                onSuccess = { _loginState.postValue(AuthState.Success("Login successful")) },
                onFailure = { _loginState.postValue(AuthState.Error(it.message ?: "Login failed")) }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        _registerState.value = AuthState.Loading
        viewModelScope.launch {
            repo.register(name, email, password).fold(
                onSuccess = { _registerState.postValue(AuthState.Success("Account created")) },
                onFailure = { _registerState.postValue(AuthState.Error(it.message ?: "Registration failed")) }
            )
        }
    }

    fun forgotPassword(email: String) {
        _forgotState.value = AuthState.Loading
        viewModelScope.launch {
            repo.forgotPassword(email).fold(
                onSuccess = { _forgotState.postValue(AuthState.Success("Reset email sent")) },
                onFailure = { _forgotState.postValue(AuthState.Error(it.message ?: "Failed")) }
            )
        }
    }

    fun loadUser(uid: String) {
        viewModelScope.launch {
            repo.getUserDataFlow(uid).collect { user ->
                _currentUser.postValue(user)
            }
        }
    }

    fun signOut() {
        repo.signOut()
        _currentUser.value = null
    }

    val isLoggedIn: Boolean get() = repo.isLoggedIn
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
