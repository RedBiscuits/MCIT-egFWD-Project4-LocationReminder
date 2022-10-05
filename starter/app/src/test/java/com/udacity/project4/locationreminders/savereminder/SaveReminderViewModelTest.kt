package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val reminder = ReminderDataItem("reminder", "description", "location", 12.0, 12.0,"1")

    @Before
    fun setUpViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun saveReminderTest_SavingDataThenClearing () = mainCoroutineRule.runBlockingTest{

        // Given
        viewModel.reminderTitle.value = reminder.title
        viewModel.reminderDescription.value = reminder.description
        viewModel.reminderSelectedLocationStr.value = reminder.location
        viewModel.latitude.value = reminder.latitude
        viewModel.longitude.value = reminder.longitude

        // When
        viewModel.onClear()

        // Then
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

    @After
    fun clearDataSource() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }
}