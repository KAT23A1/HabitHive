package com.example.habithive.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// One tiny place that holds the dark-mode choice and saves it to disk,
// so the app remembers it next time it opens.
object ThemeState {
    private const val KEY = "dark_mode"
    var isDark by mutableStateOf(false)
        private set

    fun load() {
        // reuse the same SharedPreferences the session already uses
        isDark = ServiceLocator.session
            .let { prefs() }.getBoolean(KEY, false)
    }

    fun toggle() {
        isDark = !isDark
        prefs().edit().putBoolean(KEY, isDark).apply()
    }

    private fun prefs() = appContext.getSharedPreferences("habithive", 0)

    // set once at startup
    lateinit var appContext: android.content.Context
}