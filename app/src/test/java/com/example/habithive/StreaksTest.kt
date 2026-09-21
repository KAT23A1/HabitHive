package com.example.habithive

import com.example.habithive.data.computeStreak
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for the streak calculation.
 * These run on the JVM (no phone/emulator needed), which is why
 * computeStreak was written as a pure function.
 */
class StreaksTest {

    private val today = LocalDate.of(2026, 1, 10)

    @Test
    fun noLogs_givesZeroStreak() {
        // no completed days at all -> streak is 0
        val result = computeStreak(emptySet(), today)
        assertEquals(0, result)
    }

    @Test
    fun onlyToday_givesStreakOfOne() {
        val done = setOf(today)
        assertEquals(1, computeStreak(done, today))
    }

    @Test
    fun threeConsecutiveDaysEndingToday_givesThree() {
        val done = setOf(today, today.minusDays(1), today.minusDays(2))
        assertEquals(3, computeStreak(done, today))
    }

    @Test
    fun streakEndingYesterday_stillCounts_whenTodayNotDone() {
        // today isn't ticked yet, but yesterday + the day before are:
        // an active streak should still show (2), not reset to 0
        val done = setOf(today.minusDays(1), today.minusDays(2))
        assertEquals(2, computeStreak(done, today))
    }

    @Test
    fun gapBreaksTheStreak() {
        // today + a day two days ago, but yesterday is missing -> only today counts
        val done = setOf(today, today.minusDays(2), today.minusDays(3))
        assertEquals(1, computeStreak(done, today))
    }
}