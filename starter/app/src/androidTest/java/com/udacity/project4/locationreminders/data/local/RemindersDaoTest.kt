package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private val reminder = ReminderDTO("reminder", "description", "location", 12.0, 12.0, "0")
    private val reminder1 = ReminderDTO("reminder1", "description1", "location1", 16.0, 16.0, "1")

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // GIVEN - Insert a task to DB.
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
    }

    @Test
    fun insertTasksAndGetAll() = runBlockingTest {
        // GIVEN - Insert a task.
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder1)
        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data contains the expected values.
        assertThat<List<ReminderDTO>>(loaded , notNullValue())
        assertThat(loaded.size, `is`(not(0)))

    }


    @Test
    fun insertTasksAndDeleteAll() = runBlockingTest {
        // GIVEN - Insert a task.
        database.reminderDao().deleteAllReminders()
        // WHEN - Get tasks from the database.
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded datais empty.
        assertThat(loaded.size, `is`(0))

    }

    @Test
    fun insertTaskAndDeleteById() = runBlockingTest {
        // GIVEN - Insert a task.
        database.reminderDao().saveReminder(reminder)

        // WHEN - Delete the task by id from the database and load it again.
        database.reminderDao().deleteReminderById(reminder.id)
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data doesnot exist.
        assertThat(loaded, nullValue())
    }

    @After
    fun closeDb() = database.close()
}