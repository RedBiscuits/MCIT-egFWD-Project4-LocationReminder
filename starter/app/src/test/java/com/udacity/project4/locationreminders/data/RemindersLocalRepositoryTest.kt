package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

/*
* These are two different test methods one with real DB and repository (commented code)
* while the other with the fake data source
* */

//@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersLocalRepositoryTest {


    private lateinit var fakeRepository: FakeDataSource
    private val reminder = ReminderDTO("reminder", "description", "location", 12.0, 12.0, "0")
    private val reminder1 = ReminderDTO("reminder1", "description1", "location1", 16.0, 16.0, "1")
//    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        fakeRepository = FakeDataSource(arrayListOf())
        /*
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)

         */
    }

    @Test
    fun saveReminder_getsReminderById() = runBlocking {
        // Given
        fakeRepository.saveReminder(reminder)

        // When
        val result = fakeRepository.getReminder(reminder.id) as Result.Success

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
        fakeRepository.saveReminder(reminder)
        fakeRepository.saveReminder(reminder1)

        // When
        val result = fakeRepository.getReminders() as Result.Success

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
        fakeRepository.saveReminder(reminder)
        fakeRepository.saveReminder(reminder1)

        // When
        fakeRepository.deleteReminder(reminder.id)
        val result = fakeRepository.getReminders() as Result.Success

        //Then
        assertThat(result.data.size, IsEqual(1))
        assertThat(result.data[0].id, IsEqual(reminder1.id))
    }

    @Test
    fun deleteReminders_deletesAllReminders() = runBlocking {
        // Given
        fakeRepository.saveReminder(reminder)
        fakeRepository.saveReminder(reminder1)

        // When
        fakeRepository.deleteAllReminders()
        val result = fakeRepository.getReminders() as Result.Success

        //Then
        assertThat(result.data.size, IsEqual(0))
        assertThat(result.data.isEmpty(), IsEqual(true))
    }

    @Test
    fun errorReminder_returnsErrorWhenEmpty() = runBlocking {

        // Given
        fakeRepository.deleteAllReminders()

        // When
        val result = fakeRepository.getReminder(reminder.id) as Result.Error

        // Then
        assertThat(result.message , IsEqual("Reminder not found!"))
    }
/*
    @After
    fun cleanUp() {
        database.close()
    }
*/
}