package com.example.habithive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habithive.data.ServiceLocator
import com.example.habithive.data.ThemeState

@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    // read the signed-in user's email (stored at login); fall back if missing
    val email = ServiceLocator.session.userEmail ?: "Signed in"

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // ---- Account section ----
        Text("ACCOUNT", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Status", style = MaterialTheme.typography.bodySmall)
                Text(email, style = MaterialTheme.typography.titleMedium)
            }
        }

        // ---- Preferences section ----
        Text("PREFERENCES", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Dark mode", style = MaterialTheme.typography.titleMedium)
                    Text("Switch between light and dark theme",
                        style = MaterialTheme.typography.bodySmall)
                }
                // the switch flips the whole app's theme live
                Switch(
                    checked = ThemeState.isDark,
                    onCheckedChange = { ThemeState.toggle() }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // ---- Log out ----
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Log out") }
    }
}