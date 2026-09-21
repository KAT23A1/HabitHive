package com.example.habithive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.habithive.data.ServiceLocator
import com.example.habithive.ui.theme.AuthScreen
import com.example.habithive.ui.HomeScreen
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
                        HomeScreen(onLogout = {
                            ServiceLocator.session.clear()
                            nav.navigate("auth") { popUpTo("home") { inclusive = true } }
                        })
                    }
                }
            }
        }
    }
}