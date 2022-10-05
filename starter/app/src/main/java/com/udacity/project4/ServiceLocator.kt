package com.udacity.project4

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private var database: RemindersDatabase? = null
    @Volatile
    var tasksRepository: RemindersLocalRepository? = null
        @VisibleForTesting set
    private val lock = Any()

    fun provideTasksRepository(context: Context): RemindersLocalRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): RemindersLocalRepository {
        val newRepo = RemindersLocalRepository(createTaskLocalDataSource(context),Dispatchers.Unconfined)
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): RemindersDao {
        val database = database ?: createDataBase(context)
        return database.reminderDao()
    }

    private fun createDataBase(context: Context): RemindersDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            RemindersDatabase::class.java, "locationReminders.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                tasksRepository?.deleteAllReminders()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}