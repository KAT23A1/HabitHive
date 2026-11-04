package com.example.habithive

import com.example.habithive.data.habitScore
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class HabitScoreTest {
    private val today = LocalDate.of(2026, 11, 3)
    @Test fun noLogs_isZero() = assertEquals(0, habitScore(emptySet(), today))
    @Test fun perfectMonth_isHigh() =
        assertTrue(habitScore((0L..29L).map { today.minusDays(it) }.toSet(), today) > 95)
    @Test fun oneMissedDay_isForgiving() {
        val all = (0L..29L).map { today.minusDays(it) }
        val perfect = habitScore(all.toSet(), today)
        val oneMiss = habitScore((all - today.minusDays(1)).toSet(), today)
        assertTrue(oneMiss < perfect && oneMiss > perfect - 20)
    }
}
