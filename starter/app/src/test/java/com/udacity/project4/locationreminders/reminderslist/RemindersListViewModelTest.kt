package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private val observer = Observer<List<ReminderDataItem>>{}

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun remindersViewModelTest () = mainCoroutineRule.runBlockingTest{
        // Given
        val viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),FakeDataSource())
        viewModel.remindersList.observeForever(observer)

        // When
        viewModel.loadReminders()
        val value = viewModel.remindersList.getOrAwaitValue()

        // Then
        assertThat(value, (not(nullValue())))
    }

    @Test
    fun invalidateShowNoData_showNoData_isTrue()= mainCoroutineRule.runBlockingTest{
        // Given
        fakeDataSource.deleteAllReminders()

        // When
        remindersListViewModel.loadReminders()

        // Then
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, `is`(0))
    }

    @Test
    fun loadTasks_loading() {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the view model.
        remindersListViewModel.loadReminders()

        // Then assert that the progress indicator is shown.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @After
    fun clearDataSource() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }

}