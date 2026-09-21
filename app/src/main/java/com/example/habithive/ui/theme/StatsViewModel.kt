package com.example.habithive.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habithive.data.ServiceLocator
import com.example.habithive.data.computeStreak
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// everything the Statistics screen needs to draw
data class StatsUi(
    val habitCount: Int = 0,
    val habitScore: Int = 0,          // % of the week completed
    val bestStreak: Int = 0,
    val completionsThisWeek: Int = 0,
    val weekLabels: List<String> = emptyList(),  // e.g. Mon..Sun (last 7 days)
    val weekCounts: List<Int> = emptyList(),      // completions per day
    val weekMax: Int = 1                          // used to scale the bars
)

class StatsViewModel : ViewModel() {
    var stats by mutableStateOf(StatsUi())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun load() {
        loading = true; error = null
        viewModelScope.launch {
            try {
                val habits = ServiceLocator.restApi.getHabits()
                val logs = ServiceLocator.restApi.getLogs()
                val today = LocalDate.now()

                // last 7 days, oldest first
                val last7 = (6 downTo 0).map { today.minusDays(it.toLong()) }
                val weekCounts = last7.map { d -> logs.count { it.logDate == d.toString() } }
                val weekLabels = last7.map {
                    it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                }
                val completionsThisWeek = weekCounts.sum()

                // simple score: how much of the possible week got done
                val possible = habits.size * 7
                val score = if (possible > 0)
                    (completionsThisWeek * 100 / possible).coerceAtMost(100) else 0

                // best current streak across all habits
                val best = habits.maxOfOrNull { h ->
                    val dates = logs.filter { it.habitId == h.id }
                        .mapNotNull { runCatching { LocalDate.parse(it.logDate) }.getOrNull() }
                        .toSet()
                    computeStreak(dates, today)
                } ?: 0

                val weekMax = maxOf(habits.size, weekCounts.maxOrNull() ?: 0, 1)

                stats = StatsUi(
                    habitCount = habits.size,
                    habitScore = score,
                    bestStreak = best,
                    completionsThisWeek = completionsThisWeek,
                    weekLabels = weekLabels,
                    weekCounts = weekCounts,
                    weekMax = weekMax
                )
                Log.d("STATS", "score=$score best=$best week=$weekCounts")
            } catch (e: Exception) {
                Log.e("STATS", "load failed", e)
                error = "Couldn't load statistics."
            } finally {
                loading = false
            }
        }
    }
}