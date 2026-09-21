package com.example.habithive

import android.app.Application
import com.example.habithive.data.ServiceLocator

class HabitHiveApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)   // set up networking once at startup
    }
}