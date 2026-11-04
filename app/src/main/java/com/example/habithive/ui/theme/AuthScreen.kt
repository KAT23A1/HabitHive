package com.example.habithive.ui.theme

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habithive.R


@Composable
fun AuthScreen(onAuthed: () -> Unit, vm: AuthViewModel = viewModel()) {
    var isRegister by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }   // password visibility toggle
    val context = LocalContext.current

    // leave this screen once login/register succeeds
    LaunchedEffect(vm.success) { if (vm.success) onAuthed() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(if (isRegister) R.string.create_account else R.string.welcome_back),
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = vm.email,
            onValueChange = { vm.email = it },
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // email keyboard
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = vm.password,
            onValueChange = { vm.password = it },
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // show plain text only when the user chooses to
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(stringResource(if (showPassword) R.string.hide else R.string.show))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // validation / server errors shown in red, never a crash
        vm.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(it), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { if (isRegister) vm.register() else vm.login() },
            enabled = !vm.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (vm.loading) CircularProgressIndicator(Modifier.size(20.dp))
            else Text(stringResource(if (isRegister) R.string.register else R.string.log_in))
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { vm.googleLogin(context) },
            enabled = !vm.loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.google_continue)) }

        TextButton(onClick = { isRegister = !isRegister; vm.error = null }) {
            Text(stringResource(if (isRegister) R.string.have_account else R.string.new_here))
        }
    }
}
