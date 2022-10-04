package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import net.bytebuddy.matcher.ElementMatchers.`is`
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.Null

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {


    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase
    private val reminder = ReminderDTO("reminder", "description", "location", 12.0, 12.0, "0")
    private val reminder1 = ReminderDTO("reminder1", "description1", "location1", 16.0, 16.0, "1")

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)
    }

    @Test
    fun saveReminder_getsReminderById() = runBlocking {
        // Given
        repository.saveReminder(reminder)

        // When
        val result = repository.getReminder(reminder.id) as Result.Success

        // Then
        assertThat(result.data.title, IsEqual(reminder.title))
        assertThat(result.data.description, IsEqual(reminder.description))
        assertThat(result.data.location, IsEqual(reminder.location))
        assertThat(result.data.latitude, IsEqual(reminder.latitude))
        assertThat(result.data.longitude, IsEqual(reminder.longitude))
        assertThat(result.data.id, IsEqual(reminder.id))
    }

    @Test
    fun saveReminder_getsAllReminders() = runBlocking {
        // Given
        repository.saveReminder(reminder)
        repository.saveReminder(reminder1)

        // When
        val result = repository.getReminders() as Result.Success

        // Then
        assertThat(result.data[0].title, IsEqual(reminder.title))
        assertThat(result.data[0].description, IsEqual(reminder.description))
        assertThat(result.data[0].location, IsEqual(reminder.location))
        assertThat(result.data[0].latitude, IsEqual(reminder.latitude))
        assertThat(result.data[0].longitude, IsEqual(reminder.longitude))
        assertThat(result.data[0].id, IsEqual(reminder.id))
    }

    @Test
    fun deleteReminder_deletesSpecificReminder() = runBlocking {
        // Given
        repository.saveReminder(reminder)
        repository.saveReminder(reminder1)

        // When
        repository.deleteReminder(reminder.id)
        val result = repository.getReminders() as Result.Success

        //Then
        assertThat(result.data.size, IsEqual(1))
        assertThat(result.data[0].id, IsEqual(reminder1.id))
    }

    @Test
    fun deleteReminders_deletesAllReminders() = runBlocking {
        // Given
        repository.saveReminder(reminder)
        repository.saveReminder(reminder1)

        // When
        repository.deleteAllReminders()
        val result = repository.getReminders() as Result.Success

        //Then
        assertThat(result.data.size, IsEqual(0))
        assertThat(result.data.isEmpty(), IsEqual(true))
    }

    @Test
    fun errorReminder_returnsErrorWhenEmpty() = runBlocking {

        // Given
        repository.deleteAllReminders()

        // When
        val result = repository.getReminder(reminder.id) as Result.Error

        // Then
        assertThat(result.message , IsEqual("Reminder not found!"))
    }

    @After
    fun cleanUp() {
        database.close()
    }

}