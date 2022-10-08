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

/*
*   View model and live data test
* */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // fake data source and view model
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private val observer = Observer<List<ReminderDataItem>> {}

    // app context provider
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // test coroutine
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // initialzations
    @Before
    fun setUpViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    //
    @Test
    fun remindersViewModelTest_AddDataAndCheckForValue() =
        mainCoroutineRule.runBlockingTest {

            // Given - having view model running with observer
            val viewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                FakeDataSource()
            )
            viewModel.remindersList.observeForever(observer)

            // When - loading data from viewmodel
            viewModel.loadReminders()
            val value = viewModel.remindersList.getOrAwaitValue()

            // Then - checking fetched data value
            assertThat(value, (not(nullValue())))
        }

    // no data error test
    @Test
    fun invalidateShowNoData_showNoData_isTrue() = mainCoroutineRule.runBlockingTest {
        // Given - clearing data source
        fakeDataSource.deleteAllReminders()

        // When - loading data from viewmodel to make sure everything is updated and linked
        remindersListViewModel.loadReminders()

        // Then - checking that loaded values are empty and noData works
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

    // clearing data source after all tests are done.
    @After
    fun clearDataSource() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
    }

}