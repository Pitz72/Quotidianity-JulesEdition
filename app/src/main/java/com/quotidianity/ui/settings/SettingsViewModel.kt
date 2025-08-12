package com.quotidianity.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quotidianity.auth.GoogleAuthUiClient
import com.quotidianity.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val isSignedIn: Boolean = false,
    val user: User? = null
)

class SettingsViewModel(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        // In a real app, you would check if the user is already signed in
        // and update the state accordingly.
        val signedInUser = googleAuthUiClient.getSignedInUser()
        _state.update { it.copy(isSignedIn = signedInUser != null, user = signedInUser) }
    }

    fun onSignInResult(user: User?) {
        _state.update { it.copy(
            isSignedIn = user != null,
            user = user
        )}
    }

    fun onSignOut() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
            _state.update { it.copy(
                isSignedIn = false,
                user = null
            )}
        }
    }
}

class SettingsViewModelFactory(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(googleAuthUiClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
