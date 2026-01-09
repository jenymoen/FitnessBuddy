package com.fitnessbuddy.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessbuddy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
        private set
        
    var error by mutableStateOf<String?>(null)
        private set
        
    var isLoggedIn by mutableStateOf(false)
        private set

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun signIn() {
        if (email.isBlank() || password.isBlank()) {
            error = "Please fill in all fields"
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            error = null
            
            val result = authRepository.signInWithEmail(email, password)
            
            if (result.isSuccess) {
                isLoggedIn = true
            } else {
                error = result.exceptionOrNull()?.message ?: "Login failed"
            }
            
            isLoading = false
        }
    }

    fun signUp() {
         if (email.isBlank() || password.isBlank()) {
            error = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            val result = authRepository.signUpWithEmail(email, password)

            if (result.isSuccess) {
                isLoggedIn = true
            } else {
                error = result.exceptionOrNull()?.message ?: "Sign up failed"
            }

            isLoading = false
        }
    }
}
