package com.example.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayUiState(val settings: TaskSettings, val tasks: List<Task>)

class TasksViewModel(
    private val tasksRepository: TasksRepository,
    private val settingsRepository: TaskSettingsRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val day = settingsRepository.settings
        .flatMapLatest { settings ->
            tasksRepository.observe(settings).map { DayUiState(settings, it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun chooseDate(date: LocalDate) {
        viewModelScope.launch { settingsRepository.selectDate(date) }
    }

    fun shiftDate(days: Long) {
        viewModelScope.launch { settingsRepository.shiftDate(days) }
    }

    fun chooseOrder(order: TaskOrder) {
        viewModelScope.launch { settingsRepository.selectOrder(order) }
    }

    suspend fun save(task: Task) = tasksRepository.save(task)

    suspend fun delete(task: Task) = tasksRepository.delete(task)

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TasksApplication
                TasksViewModel(app.tasksRepository, app.settingsRepository)
            }
        }
    }
}
