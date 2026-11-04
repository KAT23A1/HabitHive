package com.example.habithive.data

import java.time.LocalDate
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Counts consecutive completed days ending today.
 * If today isn't ticked yet, we count the run ending yesterday,
 * so an active streak still shows until the day is over.
 * Pure function (no Android) -> easy to unit test.
 */
fun computeStreak(doneDates: Set<LocalDate>, today: LocalDate): Int {
    var streak = 0
    var day = if (today in doneDates) today else today.minusDays(1)
    while (day in doneDates) {
        streak++
        day = day.minusDays(1)
    }
    return streak
}

/** Forgiving habit score 0-100: recent days count most, one miss only dents it. Half-life = 5 days. */
fun habitScore(done: Set<LocalDate>, today: LocalDate, windowDays: Int = 30): Int {
    val keep = 0.5.pow(1.0 / 5.0)
    var score = 0.0
    var d = today.minusDays(windowDays - 1L)
    while (!d.isAfter(today)) {
        score = score * keep + (if (d in done) 1.0 else 0.0) * (1 - keep)
        d = d.plusDays(1)
    }
    return (score * 100).roundToInt()
}
