package com.example.habithive.ui.theme

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habithive.data.Credentials
import com.example.habithive.data.ServiceLocator
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)   // screen watches this to navigate away

    // basic input validation so bad input never crashes the app (UI marks)
    private fun invalidInput(): String? = when {
        !email.contains("@") -> "Enter a valid email address"
        password.length < 6  -> "Password must be at least 6 characters"
        else -> null
    }

    fun register() = run(isRegister = true)
    fun login() = run(isRegister = false)

    private fun run(isRegister: Boolean) {
        invalidInput()?.let { error = it; return }
        loading = true; error = null
        viewModelScope.launch {
            try {
                val resp = if (isRegister)
                    ServiceLocator.authApi.signUp(Credentials(email.trim(), password))
                else
                    ServiceLocator.authApi.login(body = Credentials(email.trim(), password))

                // save the token + user id for future REST calls
                ServiceLocator.session.accessToken = resp.accessToken
                ServiceLocator.session.userId = resp.user?.id
                Log.d("AUTH", "Signed in as ${resp.user?.email}")
                success = true
            } catch (e: Exception) {
                Log.e("AUTH", "Auth failed", e)
                error = "Login failed. Check your details and try again."
            } finally {
                loading = false
            }
        }
    }
}