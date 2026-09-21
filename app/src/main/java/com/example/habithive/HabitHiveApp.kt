package com.example.habithive

import android.app.Application
import com.example.habithive.data.ServiceLocator
import com.example.habithive.data.ThemeState

class HabitHiveApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)     // set up networking
        ThemeState.appContext = this  // give ThemeState a context
        ThemeState.load()             // restore the saved dark/light choice
    }
}