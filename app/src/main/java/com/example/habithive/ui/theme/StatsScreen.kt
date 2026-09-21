package com.example.habithive.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StatsScreen(vm: StatsViewModel = viewModel()) {
    // reload each time this tab opens so numbers are fresh
    LaunchedEffect(Unit) { vm.load() }
    val s = vm.stats

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Statistics", style = MaterialTheme.typography.headlineMedium)

        if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        if (!vm.loading && s.habitCount == 0) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Add habits on the Home tab to see your stats.")
            }
            return@Column
        }

        // summary cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Habit score", "${s.habitScore}%", Modifier.weight(1f))
            StatCard("Best streak", "${s.bestStreak} 🔥", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Habits", "${s.habitCount}", Modifier.weight(1f))
            StatCard("Done this week", "${s.completionsThisWeek}", Modifier.weight(1f))
        }

        Text("This week", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth()) {
            WeeklyBars(s.weekLabels, s.weekCounts, s.weekMax)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

// a small hand-drawn bar chart — no external chart library needed
@Composable
private fun WeeklyBars(labels: List<String>, counts: List<Int>, max: Int) {
    val maxBarHeight = 150.dp
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        counts.forEachIndexed { i, count ->
            val frac = if (max > 0) count.toFloat() / max else 0f
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$count", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .width(22.dp)
                        .height(maxBarHeight * frac.coerceAtLeast(0.02f))
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.height(4.dp))
                Text(labels.getOrElse(i) { "" }, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}