package com.example.tasks

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskStorageTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val day = LocalDate.of(2026, 11, 27)

    @Test
    fun savesEditsDeletesAndSortsTasksForSelectedDay() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, TasksDatabase::class.java).build()
        try {
            val repo = TasksRepository(db.taskDao())
            val today = TaskSettings(day, TaskOrder.EARLIEST)
            repo.save(Task(title = "Яблоко", description = "", epochDay = day.toEpochDay(), minuteOfDay = 540))
            repo.save(Task(title = "Атлас", description = "", epochDay = day.toEpochDay(), minuteOfDay = 720))
            repo.save(Task(title = "Завтра", description = "", epochDay = day.plusDays(1).toEpochDay(), minuteOfDay = 540))

            val tasks = repo.observe(today).first()
            assertEquals(listOf("Яблоко", "Атлас"), tasks.map { it.title })
            assertEquals(tasks.reversed(), repo.observe(today.copy(order = TaskOrder.LATEST)).first())
            assertEquals(listOf("Атлас", "Яблоко"), repo.observe(today.copy(order = TaskOrder.TITLE)).first().map { it.title })

            val edited = tasks.first().copy(title = "Покупки", description = "Купить фрукты")
            repo.save(edited)
            assertEquals(edited, repo.observe(today).first().first())
            repo.delete(edited)
            assertEquals(listOf("Атлас"), repo.observe(today).first().map { it.title })
        } finally {
            db.close()
        }
    }

    @Test
    fun restoresSelectedDateAndOrderFromDisk() = runBlocking {
        val directory = kotlin.io.path.createTempDirectory(context.cacheDir.toPath(), "task-settings-").toFile()
        val file = File(directory, "tasks.preferences_pb")
        val writeJob = SupervisorJob()
        val readJob = SupervisorJob()
        try {
            val writer = TaskSettingsRepository(
                PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + writeJob)) { file }
            )
            writer.selectDate(day)
            writer.shiftDate(2)
            writer.selectOrder(TaskOrder.TITLE)
            writeJob.cancelAndJoin()

            val reader = TaskSettingsRepository(
                PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + readJob)) { file }
            )
            assertEquals(TaskSettings(day.plusDays(2), TaskOrder.TITLE), reader.settings.first())
        } finally {
            writeJob.cancelAndJoin()
            readJob.cancelAndJoin()
            directory.deleteRecursively()
        }
    }
}
