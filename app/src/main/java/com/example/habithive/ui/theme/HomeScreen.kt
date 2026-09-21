package com.example.habithive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(onLogout: () -> Unit, vm: HomeViewModel = viewModel()) {
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<HabitUi?>(null) }
    var nameInput by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {

        Column(Modifier.fillMaxSize().padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("HabitHive", style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = onLogout) { Text("Log out") }
            }
            Spacer(Modifier.height(8.dp))

            if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (!vm.loading && vm.habits.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No habits yet. Tap + to add one.")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.habits) { ui ->
                    Card {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = ui.doneToday,
                                onCheckedChange = { vm.toggleToday(ui) }
                            )
                            Column(Modifier.weight(1f)) {
                                Text(ui.habit.name, style = MaterialTheme.typography.titleMedium)
                                Text("🔥 ${ui.streak} day streak",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { editing = ui; nameInput = ui.habit.name }) {
                                Text("Edit")
                            }
                            TextButton(onClick = { ui.habit.id?.let { vm.deleteHabit(it) } }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }

        // + button floats bottom-right (no Scaffold needed here anymore)
        FloatingActionButton(
            onClick = { nameInput = ""; showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall)
        }
    }

    if (showAdd || editing != null) {
        val isEdit = editing != null
        AlertDialog(
            onDismissRequest = { showAdd = false; editing = null },
            title = { Text(if (isEdit) "Edit habit" else "New habit") },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Habit name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (isEdit) vm.renameHabit(editing!!.habit.id!!, nameInput)
                    else vm.addHabit(nameInput)
                    showAdd = false; editing = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false; editing = null }) { Text("Cancel") }
            }
        )
    }
}