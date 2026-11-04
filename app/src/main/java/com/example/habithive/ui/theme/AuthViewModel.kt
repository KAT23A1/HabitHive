package com.example.habithive.ui.theme

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.habithive.BuildConfig
import com.example.habithive.R
import com.example.habithive.data.Credentials
import com.example.habithive.data.IdTokenRequest
import com.example.habithive.data.ServiceLocator
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
    var error by mutableStateOf<Int?>(null)   // holds a string resource id
    var success by mutableStateOf(false)   // screen watches this to navigate away

    // basic input validation so bad input never crashes the app
    private fun invalidInput(): Int? = when {
        !email.contains("@") -> R.string.err_email
        password.length < 6  -> R.string.err_password
        else -> null
    }

    fun register() = run(isRegister = true)
    fun login() = run(isRegister = false)

    /** Google sign-in: Google gives an ID token, Supabase swaps it for a session. */
    fun googleLogin(context: Context) {
        loading = true; error = null
        viewModelScope.launch {
            try {
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID).build())
                    .build()
                val cred = CredentialManager.create(context).getCredential(context, request).credential
                if (cred is CustomCredential &&
                    cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val idToken = GoogleIdTokenCredential.createFrom(cred.data).idToken
                    val resp = ServiceLocator.authApi.signInWithIdToken(body = IdTokenRequest("google", idToken))
                    ServiceLocator.session.accessToken = resp.accessToken
                    ServiceLocator.session.userId = resp.user?.id
                    ServiceLocator.session.userEmail = resp.user?.email
                    Log.d("AUTH", "Google sign-in as ${resp.user?.email}")
                    success = true
                } else error = R.string.err_login
            } catch (e: GetCredentialCancellationException) {
                Log.d("AUTH", "Google sign-in cancelled by user")
            } catch (e: Exception) {
                Log.e("AUTH", "Google sign-in failed", e); error = R.string.err_login
            } finally { loading = false }
        }
    }

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
                ServiceLocator.session.userEmail = resp.user?.email
                Log.d("AUTH", "Signed in as ${resp.user?.email}")
                success = true
            } catch (e: Exception) {
                Log.e("AUTH", "Auth failed", e)
                error = R.string.err_login
            } finally {
                loading = false
            }
        }
    }
}
