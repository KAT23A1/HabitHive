package com.example.habithive.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habithive.data.Habit
import com.example.habithive.data.ServiceLocator
import com.example.habithive.data.computeStreak
import kotlinx.coroutines.launch
import java.time.LocalDate

// what each row on the home screen needs to show
data class HabitUi(val habit: Habit, val doneToday: Boolean, val streak: Int)

class HomeViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    var habits by mutableStateOf<List<HabitUi>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var offline by mutableStateOf(false)     // drives the offline banner
    var pending by mutableStateOf(0)         // changes not yet on the server

    // when the internet comes back, sync automatically
    private val netCallback = repo.registerNetworkCallback { load() }

    init { load() }
    override fun onCleared() { repo.unregisterNetworkCallback(netCallback) }

    // build the list from the PHONE database
    private suspend fun showLocal() {
        val list = repo.habits(); val logs = repo.logs()
        val today = LocalDate.now(); val todayStr = today.toString()
        habits = list.map { h ->
            val mine = logs.filter { it.habitId == h.id }
            val dates = mine.mapNotNull { runCatching { LocalDate.parse(it.logDate) }.getOrNull() }.toSet()
            HabitUi(h, mine.any { it.logDate == todayStr }, computeStreak(dates, today))
        }
        pending = repo.pendingCount()
        offline = !repo.isOnline()
    }

    fun load() {
        viewModelScope.launch {
            loading = true; error = null
            try { showLocal(); repo.sync(); showLocal() }
            catch (e: Exception) { Log.e("HABITS", "load failed", e); error = "Couldn't load your habits." }
            finally { loading = false }
        }
    }

    // every change: save on phone -> show it instantly -> try to sync
    private fun change(block: suspend () -> Unit) {
        viewModelScope.launch {
            try { block(); showLocal(); repo.sync(); showLocal() }
            catch (e: Exception) { Log.e("HABITS", "change failed", e); error = "Couldn't save that change." }
        }
    }
    fun addHabit(name: String) { if (name.isBlank()) return; change { repo.addHabit(name.trim()) } }
    fun renameHabit(id: String, name: String) { if (name.isBlank()) return; change { repo.renameHabit(id, name.trim()) } }
    fun deleteHabit(id: String) = change { repo.deleteHabit(id) }
    fun toggleToday(ui: HabitUi) {
        val id = ui.habit.id ?: return
        change { repo.setDoneToday(id, LocalDate.now().toString(), !ui.doneToday) }
    }
}
