package com.example.habithive.data

import java.time.LocalDate

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