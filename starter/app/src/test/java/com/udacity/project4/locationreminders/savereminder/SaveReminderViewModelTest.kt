package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val reminder = ReminderDataItem("reminder", "description", "location", 12.0, 12.0,"1")

    @Test
    fun saveRemindersViewModelTest (){
        val viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeDataSource()
        )

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
}