package com.example.habithive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import com.example.habithive.data.ServiceLocator
import com.example.habithive.ui.theme.AuthScreen
import com.example.habithive.ui.MainScreen
import com.example.habithive.ui.theme.HabitHiveTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val notifPermission = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureNotificationChannel(this)
        if (android.os.Build.VERSION.SDK_INT >= 33)
            notifPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token
            .addOnSuccessListener { android.util.Log.d("FCM", "Token: $it") }

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
                            lifecycleScope.launch {
                                ServiceLocator.repo.sync()
                                ServiceLocator.repo.wipe()
                                ServiceLocator.session.clear()
                                nav.navigate("auth") { popUpTo("main") { inclusive = true } }
                            }
                        })
                    }
                }
            }
        }
    }
}
