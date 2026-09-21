package com.example.habithive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.habithive.data.ServiceLocator
import com.example.habithive.ui.theme.AuthScreen
import com.example.habithive.ui.MainScreen
import com.example.habithive.ui.theme.HabitHiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitHiveTheme(darkTheme = com.example.habithive.data.ThemeState.isDark) {
                val nav = rememberNavController()
                val start = if (ServiceLocator.session.isLoggedIn) "main" else "auth"

                NavHost(navController = nav, startDestination = start) {
                    composable("auth") {
                        AuthScreen(onAuthed = {
                            nav.navigate("main") { popUpTo("auth") { inclusive = true } }
                        })
                    }
                    composable("main") {
                        MainScreen(onLogout = {
                            ServiceLocator.session.clear()
                            nav.navigate("auth") { popUpTo("main") { inclusive = true } }
                        })
                    }
                }
            }
        }
    }
}