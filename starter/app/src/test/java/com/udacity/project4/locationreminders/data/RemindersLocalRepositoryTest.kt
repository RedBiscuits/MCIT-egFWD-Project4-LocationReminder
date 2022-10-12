package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.FakeDataSource
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/*
* Local repository test
* */

@ExperimentalCoroutinesApi
class RemindersLocalRepositoryTest {


    // Data source and reminders initializations
    private lateinit var fakeRepository: FakeDataSource
    private val reminder = ReminderDTO("reminder", "description", "location", 12.0, 12.0, "0")
    private val reminder1 = ReminderDTO("reminder1", "description1", "location1", 16.0, 16.0, "1")

    // test coroutine
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // initializing data source
    @Before
    fun setup() {
        fakeRepository = FakeDataSource(arrayListOf())
    }

    // save reminder then get the reminder again by ID
    @Test
    fun saveReminder_getsReminderById() = mainCoroutineRule.runBlockingTest {
        // Given - saving reminder by repo
        fakeRepository.saveReminder(reminder)

        // When - getting reminder if exist
        val result = fakeRepository.getReminder(reminder.id) as Result.Success

        // Then - checking if retreived data matchs what's displayed
        assertThat(result.data.title, IsEqual(reminder.title))
        assertThat(result.data.description, IsEqual(reminder.description))
        assertThat(result.data.location, IsEqual(reminder.location))
        assertThat(result.data.latitude, IsEqual(reminder.latitude))
        assertThat(result.data.longitude, IsEqual(reminder.longitude))
        assertThat(result.data.id, IsEqual(reminder.id))
    }

    //save few reminders then fetch em all
    @Test
    fun saveReminder_getsAllReminders() = mainCoroutineRule.runBlockingTest {
        // Given - saving some reminders to repo
        fakeRepository.saveReminder(reminder)
        fakeRepository.saveReminder(reminder1)

        // When - fetching all reminders
        val result = fakeRepository.getReminders() as Result.Success

        // Then - checking if the retrieved data matchs what's displayed
        assertThat(result.data[0].title, IsEqual(reminder.title))
        assertThat(result.data[0].description, IsEqual(reminder.description))
        assertThat(result.data[0].location, IsEqual(reminder.location))
        assertThat(result.data[0].latitude, IsEqual(reminder.latitude))
        assertThat(result.data[0].longitude, IsEqual(reminder.longitude))
        assertThat(result.data[0].id, IsEqual(reminder.id))
    }

    // save reminders to repo then deleting first and checking if the first one is the old second
    @Test
    fun deleteReminder_deletesSpecificReminder() = mainCoroutineRule.runBlockingTest {
        // Given - adding 2 reminders to repo
        fakeRepository.saveReminder(reminder)
        fakeRepository.saveReminder(reminder1)

        // When - deleting the first reminder by ID and fetching all reminders
        fakeRepository.deleteReminder(reminder.id)
        val result = fakeRepository.getReminders() as Result.Success

        //Then - checks if the fetched reminders has the right order and data
        assertThat(result.data.size, IsEqual(1))
        assertThat(result.data[0].id, IsEqual(reminder1.id))
    }

    //saving reminders then deleting all reminders
    @Test
    fun deleteReminders_deletesAllReminders() = mainCoroutineRule.runBlockingTest {
        // Given - adding 2 reminders to repo
        fakeRepository.saveReminder(reminder)
        fakeRepository.saveReminder(reminder1)

        // When - deleting all reminders then fetching what exists in repo
        fakeRepository.deleteAllReminders()
        val result = fakeRepository.getReminders() as Result.Success

        //Then - checks if the retrieved data is empty
        assertThat(result.data.size, IsEqual(0))
        assertThat(result.data.isEmpty(), IsEqual(true))
    }

    // testing repo for errors
    @Test
    fun errorReminder_returnsErrorWhenEmpty() = mainCoroutineRule.runBlockingTest {

        // Given - making sure repo is clear of tasks
        fakeRepository.deleteAllReminders()

        // When -- getting task by ID to error
        val result = fakeRepository.getReminder(reminder.id) as Result.Error

        // Then - checking id the result is error as supposed to be
        assertThat(result.message , IsEqual("Reminder not found!"))
    }

}