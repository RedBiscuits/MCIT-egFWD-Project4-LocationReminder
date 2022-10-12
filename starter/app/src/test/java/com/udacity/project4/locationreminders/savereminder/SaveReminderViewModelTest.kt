package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.FakeDataSource
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

/*
*   View model and live data test
* */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // fake data source and view model
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    // test main coroutine
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // app context provider
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val reminder = ReminderDataItem("reminder", "description", "location", 12.0, 12.0,"1")

    // initializations
    @Before
    fun setUpViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    // saving reminder then clearing all reminders and check all is deleted
    @Test
    fun saveReminderTest_SavingDataThenClearing () = mainCoroutineRule.runBlockingTest{

        // Given - adding reminder data to VM
        viewModel.reminderTitle.value = reminder.title
        viewModel.reminderDescription.value = reminder.description
        viewModel.reminderSelectedLocationStr.value = reminder.location
        viewModel.latitude.value = reminder.latitude
        viewModel.longitude.value = reminder.longitude

        // When - clearing all data from VM
        viewModel.onClear()

        // Then - checking LiveData is empty
        MatcherAssert.assertThat(
            viewModel.reminderTitle.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            viewModel.reminderDescription.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            viewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            viewModel.latitude.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            viewModel.longitude.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
    }

    // clearing fake data source
    @After
    fun clearDataSource() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }
}