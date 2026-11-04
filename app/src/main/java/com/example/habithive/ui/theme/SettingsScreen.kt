package com.example.habithive.ui

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.habithive.R
import com.example.habithive.data.ServiceLocator
import com.example.habithive.data.ThemeState

@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    // read the signed-in user's email (stored at login); fall back if missing
    val signedInText = stringResource(R.string.signed_in)
    val email = ServiceLocator.session.userEmail ?: signedInText

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)

        // ---- Account section ----
        Text(stringResource(R.string.account), style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.status), style = MaterialTheme.typography.bodySmall)
                Text(email, style = MaterialTheme.typography.titleMedium)
            }
        }

        // ---- Preferences section ----
        Text(stringResource(R.string.preferences), style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.dark_mode), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.dark_mode_desc),
                        style = MaterialTheme.typography.bodySmall)
                }
                // the switch flips the whole app's theme live
                Switch(
                    checked = ThemeState.isDark,
                    onCheckedChange = { ThemeState.toggle() }
                )
            }
        }

        val current = AppCompatDelegate.getApplicationLocales().toLanguageTags().ifEmpty { "en" }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en" to "English", "af" to "Afrikaans", "zu" to "isiZulu").forEach { (tag, label) ->
                        if (current.startsWith(tag)) Button(onClick = {}) { Text(label) }
                        else OutlinedButton(onClick = {
                            Log.d("HABITS", "Language -> $tag")
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                        }) { Text(label) }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // ---- Log out ----
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.log_out)) }
    }
}
