package com.example.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
private val lightColors = lightColorScheme(
    primary = Color(0xFF345F48),
    primaryContainer = Color(0xFFBFE9CC),
    onPrimaryContainer = Color(0xFF173625),
    surface = Color(0xFFF9FAF5)
)
private val darkColors = darkColorScheme(
    primary = Color(0xFFA5D5B5),
    primaryContainer = Color(0xFF244D37),
    onPrimaryContainer = Color(0xFFE0F5E5)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors) {
                TasksScreen(viewModel(factory = TasksViewModel.Factory))
            }
        }
    }
}

@Composable
private fun TasksScreen(viewModel: TasksViewModel) {
    val state by viewModel.day.collectAsStateWithLifecycle()
    val day = state

    // A null ID closes the dialog; zero identifies a new task.
    var editorId by rememberSaveable { mutableStateOf<Long?>(null) }
    var detailsId by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        floatingActionButton = {
            if (day != null) {
                ExtendedFloatingActionButton(onClick = { editorId = 0L }) {
                    Text("＋  Новая задача")
                }
            }
        }
    ) { padding ->
        if (day == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(padding).widthIn(max = 720.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "Задачи",
                    Modifier.padding(top = 20.dp, bottom = 16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                DayHeader(day.settings.date, viewModel::chooseDate, viewModel::shiftDate)
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Задач: ${day.tasks.size}", style = MaterialTheme.typography.titleMedium)
                    SortMenu(day.settings.order, viewModel::chooseOrder)
                }
                HorizontalDivider()
                if (day.tasks.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("На этот день задач нет")
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
                        items(day.tasks, key = { it.id }) { task ->
                            ListItem(
                                modifier = Modifier.clickable { detailsId = task.id },
                                headlineContent = { Text(task.title) },
                                supportingContent = if (task.description.isBlank()) null else {
                                    {
                                        Text(task.description, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                },
                                leadingContent = {
                                    Text(
                                        task.time.format(timeFormat),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            val details = day.tasks.firstOrNull { it.id == detailsId }
            if (details != null) {
                DetailsDialog(
                    task = details,
                    onClose = { detailsId = null },
                    onEdit = {
                        detailsId = null
                        editorId = details.id
                    },
                    onDelete = viewModel::delete
                )
            }

            val editorTask = day.tasks.firstOrNull { it.id == editorId }
            if (editorId == 0L || editorTask != null) {
                TaskEditor(
                    task = editorTask,
                    defaultDate = day.settings.date,
                    onClose = { editorId = null },
                    onSave = { task ->
                        viewModel.save(task)
                        editorId = null
                        if (task.date != day.settings.date) viewModel.chooseDate(task.date)
                    }
                )
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate, onDate: (LocalDate) -> Unit, onShift: (Long) -> Unit) {
    val context = LocalContext.current
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onShift(-1) },
                modifier = Modifier.semantics { contentDescription = "Предыдущий день" }
            ) { Text("‹", style = MaterialTheme.typography.headlineMedium) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date.format(dateFormat), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showDatePicker(context, date, onDate) }) {
                    Text("Выбрать дату")
                }
            }
            TextButton(
                onClick = { onShift(1) },
                modifier = Modifier.semantics { contentDescription = "Следующий день" }
            ) { Text("›", style = MaterialTheme.typography.headlineMedium) }
        }
    }
}

@Composable
private fun SortMenu(order: TaskOrder, onOrder: (TaskOrder) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) { Text(order.label) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TaskOrder.entries.forEach { choice ->
                DropdownMenuItem(
                    text = { Text(choice.label) },
                    onClick = {
                        expanded = false
                        onOrder(choice)
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailsDialog(task: Task, onClose: () -> Unit, onEdit: () -> Unit, onDelete: suspend (Task) -> Unit) {
    var confirming by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { if (!saving) onClose() },
        title = { Text(if (confirming) "Удалить задачу?" else task.title) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (confirming) {
                    Text(task.title)
                } else {
                    Text("${task.date.format(dateFormat)} · ${task.time.format(timeFormat)}")
                    if (task.description.isNotBlank()) Text(task.description)
                }
                if (error) Text("Не удалось удалить задачу", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            if (confirming) {
                Button(enabled = !saving, onClick = {
                    saving = true
                    scope.launch {
                        try {
                            onDelete(task)
                            onClose()
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: Exception) {
                            error = true
                        } finally {
                            saving = false
                        }
                    }
                }) { Text("Удалить") }
            } else {
                Button(onClick = onEdit) { Text("Редактировать") }
            }
        },
        dismissButton = {
            Row {
                if (!confirming) TextButton(onClick = { confirming = true }) { Text("Удалить") }
                TextButton(enabled = !saving, onClick = {
                    if (confirming) {
                        confirming = false
                        error = false
                    } else {
                        onClose()
                    }
                }) { Text(if (confirming) "Отмена" else "Закрыть") }
            }
        },
        properties = DialogProperties(dismissOnClickOutside = !saving, dismissOnBackPress = !saving)
    )
}

@Composable
private fun TaskEditor(task: Task?, defaultDate: LocalDate, onClose: () -> Unit, onSave: suspend (Task) -> Unit) {
    var title by rememberSaveable { mutableStateOf(task?.title.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(task?.description.orEmpty()) }
    var day by rememberSaveable { mutableStateOf(task?.epochDay ?: defaultDate.toEpochDay()) }
    var minute by rememberSaveable { mutableStateOf(task?.minuteOfDay ?: 9 * 60) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val date = LocalDate.ofEpochDay(day)
    val time = LocalTime.of(minute / 60, minute % 60)

    AlertDialog(
        onDismissRequest = { if (!saving) onClose() },
        title = { Text(if (task == null) "Новая задача" else "Редактирование") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedButton(
                    onClick = { showDatePicker(context, date) { day = it.toEpochDay() } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(date.format(dateFormat)) }
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(context, { _, hour, minutes -> minute = hour * 60 + minutes },
                            time.hour, time.minute, true).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(time.format(timeFormat)) }
                if (error) Text("Не удалось сохранить задачу", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(enabled = title.isNotBlank() && !saving, onClick = {
                val updated = Task(task?.id ?: 0L, title.trim(), description.trim(), day, minute)
                saving = true
                scope.launch {
                    try {
                        onSave(updated)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        error = true
                    } finally {
                        saving = false
                    }
                }
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(enabled = !saving, onClick = onClose) { Text("Отмена") } },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = !saving)
    )
}

private fun showDatePicker(context: Context, initial: LocalDate, onDate: (LocalDate) -> Unit) {
    DatePickerDialog(
        context,
        { _, year, month, day -> onDate(LocalDate.of(year, month + 1, day)) },
        initial.year,
        initial.monthValue - 1,
        initial.dayOfMonth
    ).show()
}
