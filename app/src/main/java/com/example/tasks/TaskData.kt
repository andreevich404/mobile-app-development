package com.example.tasks

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import java.text.Collator
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "tasks", indices = [Index("epochDay")])
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val epochDay: Long,
    val minuteOfDay: Int
) {
    val date: LocalDate get() = LocalDate.ofEpochDay(epochDay)
    val time: LocalTime get() = LocalTime.of(minuteOfDay / 60, minuteOfDay % 60)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE epochDay = :day ORDER BY minuteOfDay, id")
    fun observeDay(day: Long): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

enum class TaskOrder(val label: String) {
    EARLIEST("По дате и времени ↑"),
    LATEST("По дате и времени ↓"),
    TITLE("По алфавиту")
}

data class TaskSettings(val date: LocalDate, val order: TaskOrder)

class TaskSettingsRepository(private val store: DataStore<Preferences>) {
    private val dateKey = longPreferencesKey("selected_date")
    private val orderKey = stringPreferencesKey("task_order")

    val settings: Flow<TaskSettings> = store.data.map { preferences ->
        TaskSettings(
            date = preferences[dateKey]?.let(LocalDate::ofEpochDay) ?: LocalDate.now(),
            order = TaskOrder.entries.firstOrNull { it.name == preferences[orderKey] }
                ?: TaskOrder.EARLIEST
        )
    }

    suspend fun selectDate(date: LocalDate) {
        store.edit { it[dateKey] = date.toEpochDay() }
    }

    suspend fun shiftDate(days: Long) {
        store.edit { preferences ->
            val date = preferences[dateKey] ?: LocalDate.now().toEpochDay()
            preferences[dateKey] = date + days
        }
    }

    suspend fun selectOrder(order: TaskOrder) {
        store.edit { it[orderKey] = order.name }
    }
}

class TasksRepository(private val dao: TaskDao) {
    fun observe(settings: TaskSettings): Flow<List<Task>> =
        dao.observeDay(settings.date.toEpochDay()).map { tasks ->
            when (settings.order) {
                TaskOrder.EARLIEST -> tasks
                TaskOrder.LATEST -> tasks.asReversed()
                TaskOrder.TITLE -> {
                    val collator = Collator.getInstance(Locale.getDefault()).apply {
                        strength = Collator.PRIMARY
                    }
                    tasks.sortedWith { first, second -> collator.compare(first.title, second.title) }
                }
            }
        }

    suspend fun save(task: Task) {
        require(task.title.isNotBlank() && task.minuteOfDay in 0..1439)
        if (task.id == 0L) dao.insert(task) else dao.update(task)
    }

    suspend fun delete(task: Task) = dao.delete(task)
}

private val Context.taskPreferences by preferencesDataStore(name = "task_preferences")

class TasksApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, TasksDatabase::class.java, "tasks.db").build()
    }

    val tasksRepository by lazy { TasksRepository(database.taskDao()) }
    val settingsRepository by lazy { TaskSettingsRepository(taskPreferences) }
}
