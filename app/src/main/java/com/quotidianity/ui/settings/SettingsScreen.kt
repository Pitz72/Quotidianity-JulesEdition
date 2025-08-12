package com.quotidianity.ui.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quotidianity.auth.GoogleAuthUiClient
import com.quotidianity.data.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val googleAuthUiClient by remember {
        lazy { GoogleAuthUiClient.create(context) }
    }

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(googleAuthUiClient)
    )
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    val user = googleAuthUiClient.signInWithIntent(result.data ?: return@launch)
                    viewModel.onSignInResult(user)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isSignedIn) {
                UserSection(user = state.user!!, onSignOut = { viewModel.onSignOut() })
            } else {
                Button(onClick = {
                    scope.launch {
                        val signInIntent = googleAuthUiClient.signIn()
                        launcher.launch(signInIntent)
                    }
                }) {
                    Text("Sign in with Google")
                }
            }
        }
    }
}

@Composable
fun UserSection(
    user: User,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Signed in as ${user.name}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.email)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSignOut) {
            Text("Sign Out")
        }
    }
}
