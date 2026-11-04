package com.example.habithive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habithive.R

@Composable
fun HomeScreen(onLogout: () -> Unit, vm: HomeViewModel = viewModel()) {
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<HabitUi?>(null) }
    var deleting by remember { mutableStateOf<HabitUi?>(null) }   // habit pending delete-confirm
    var nameInput by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {

        Column(Modifier.fillMaxSize().padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = onLogout) { Text(stringResource(R.string.log_out)) }
            }
            Spacer(Modifier.height(4.dp))
            if (vm.offline || vm.pending > 0) {
                Text(
                    if (vm.offline) stringResource(R.string.offline_banner)
                    else stringResource(R.string.pending_sync, vm.pending),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))

            if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            // an error while habits ARE shown (e.g. an add/delete failed)
            if (vm.error != null && vm.habits.isNotEmpty()) {
                Text(vm.error!!, color = MaterialTheme.colorScheme.error)
            }

            // empty state: either a load error (offer Retry) or genuinely no habits
            if (!vm.loading && vm.habits.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (vm.error != null) {
                            Text(stringResource(R.string.load_failed))
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.load() }) { Text(stringResource(R.string.retry)) }
                        } else {
                            Text(stringResource(R.string.no_habits))
                        }
                    }
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
                                Text(
                                    ui.habit.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,                       // long names won't
                                    overflow = TextOverflow.Ellipsis    // break the layout
                                )
                                Text("🔥 ${ui.streak} day streak",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { editing = ui; nameInput = ui.habit.name }) {
                                Text(stringResource(R.string.edit))
                            }
                            TextButton(onClick = { deleting = ui }) {   // ask first
                                Text(stringResource(R.string.delete))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { nameInput = ""; showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall)
        }
    }

    // ---- add / edit dialog ----
    if (showAdd || editing != null) {
        val isEdit = editing != null
        AlertDialog(
            onDismissRequest = { showAdd = false; editing = null },
            title = { Text(stringResource(if (isEdit) R.string.edit_habit else R.string.new_habit)) },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(R.string.habit_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = nameInput.isNotBlank(),   // can't save an empty habit
                    onClick = {
                        if (isEdit) vm.renameHabit(editing!!.habit.id!!, nameInput)
                        else vm.addHabit(nameInput)
                        showAdd = false; editing = null
                    }
                ) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false; editing = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    // ---- delete confirmation ----
    deleting?.let { target ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(stringResource(R.string.delete_habit_q)) },
            text = { Text(stringResource(R.string.delete_habit_body, target.habit.name)) },
            confirmButton = {
                TextButton(onClick = {
                    target.habit.id?.let { vm.deleteHabit(it) }
                    deleting = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
