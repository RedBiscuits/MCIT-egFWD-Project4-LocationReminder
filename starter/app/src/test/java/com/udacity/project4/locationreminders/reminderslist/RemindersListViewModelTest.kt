package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val observer = Observer<List<ReminderDataItem>>{}

    @Test
    fun remindersViewModelTest (){
        val viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),FakeDataSource())

        viewModel.remindersList.observeForever(observer)
        //when
        viewModel.loadReminders()

        val value = viewModel.remindersList.getOrAwaitValue()
        assertThat(value, (not(nullValue())))
    }
}