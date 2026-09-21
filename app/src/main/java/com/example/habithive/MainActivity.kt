package com.example.habithive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.habithive.data.ServiceLocator
import com.example.habithive.ui.theme.AuthScreen
import com.example.habithive.ui.theme.HabitHiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitHiveTheme {
                val nav = rememberNavController()
                val start = if (ServiceLocator.session.isLoggedIn) "home" else "auth"

                NavHost(navController = nav, startDestination = start) {
                    composable("auth") {
                        AuthScreen(onAuthed = {
                            nav.navigate("home") { popUpTo("auth") { inclusive = true } }
                        })
                    }
                    composable("home") {
                        HomePlaceholder(onLogout = {
                            ServiceLocator.session.clear()
                            nav.navigate("auth") { popUpTo("home") { inclusive = true } }
                        })
                    }
                }
            }
        }
    }
}

// temporary — Phase 3 replaces this with the real habit list
@Composable
fun HomePlaceholder(onLogout: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("You're signed in ✅", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogout) { Text("Log out") }
    }
}