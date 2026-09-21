package com.example.habithive.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habithive.data.Habit
import com.example.habithive.data.HabitLog
import com.example.habithive.data.ServiceLocator
import com.example.habithive.data.computeStreak
import kotlinx.coroutines.launch
import java.time.LocalDate

// what each row on the home screen needs to show
data class HabitUi(val habit: Habit, val doneToday: Boolean, val streak: Int)

class HomeViewModel : ViewModel() {
    var habits by mutableStateOf<List<HabitUi>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init { load() }

    // READ: pull habits + logs from the REST API and combine them
    fun load() {
        loading = true; error = null
        viewModelScope.launch {
            try {
                val api = ServiceLocator.restApi
                val habitList = api.getHabits()
                val logs = api.getLogs()
                val today = LocalDate.now()
                val todayStr = today.toString()

                habits = habitList.map { h ->
                    val myLogs = logs.filter { it.habitId == h.id }
                    val doneToday = myLogs.any { it.logDate == todayStr }
                    val doneDates = myLogs
                        .mapNotNull { runCatching { LocalDate.parse(it.logDate) }.getOrNull() }
                        .toSet()
                    HabitUi(h, doneToday, computeStreak(doneDates, today))
                }
                Log.d("HABITS", "Loaded ${habits.size} habits")
            } catch (e: Exception) {
                Log.e("HABITS", "load failed", e)
                error = "Couldn't load habits. Check your connection."
            } finally {
                loading = false
            }
        }
    }

    // CREATE
    fun addHabit(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try { ServiceLocator.restApi.addHabit(Habit(name = name.trim())); load() }
            catch (e: Exception) { Log.e("HABITS", "add failed", e); error = "Couldn't add habit." }
        }
    }

    // UPDATE (rename)
    fun renameHabit(id: String, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try { ServiceLocator.restApi.updateHabit("eq.$id", mapOf("name" to name.trim())); load() }
            catch (e: Exception) { Log.e("HABITS", "update failed", e); error = "Couldn't update habit." }
        }
    }

    // DELETE
    fun deleteHabit(id: String) {
        viewModelScope.launch {
            try { ServiceLocator.restApi.deleteHabit("eq.$id"); load() }
            catch (e: Exception) { Log.e("HABITS", "delete failed", e); error = "Couldn't delete habit." }
        }
    }

    // LOG: tick / untick "done today"
    fun toggleToday(ui: HabitUi) {
        val habitId = ui.habit.id ?: return
        val today = LocalDate.now().toString()
        viewModelScope.launch {
            try {
                if (ui.doneToday) {
                    val todays = ServiceLocator.restApi.getLogs()
                        .firstOrNull { it.habitId == habitId && it.logDate == today }
                    todays?.id?.let { ServiceLocator.restApi.deleteLog("eq.$it") }
                } else {
                    ServiceLocator.restApi.addLog(HabitLog(habitId = habitId, logDate = today))
                }
                load()
            } catch (e: Exception) {
                Log.e("HABITS", "toggle failed", e); error = "Couldn't update habit."
            }
        }
    }
}