package com.example.habithive.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(onAuthed: () -> Unit, vm: AuthViewModel = viewModel()) {
    var isRegister by remember { mutableStateOf(false) }

    // when login/register succeeds, leave this screen
    LaunchedEffect(vm.success) { if (vm.success) onAuthed() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HabitHive", style = MaterialTheme.typography.headlineLarge)
        Text(if (isRegister) "Create your account" else "Welcome back",
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = vm.email, onValueChange = { vm.email = it },
            label = { Text("Email") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = vm.password, onValueChange = { vm.password = it },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        vm.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { if (isRegister) vm.register() else vm.login() },
            enabled = !vm.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (vm.loading) CircularProgressIndicator(Modifier.size(20.dp))
            else Text(if (isRegister) "Register" else "Log in")
        }

        TextButton(onClick = { isRegister = !isRegister; vm.error = null }) {
            Text(if (isRegister) "Have an account? Log in" else "New here? Create an account")
        }
    }
}