package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val reminder = ReminderDTO("reminder", "description", "location", 12.0, 12.0,"1")
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun showReminderSavedToast() = runBlocking{

        // Given - launching the activity for test
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // When -
        // Clicking addFAb then inputing text to new launched fragment
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(
            ViewActions.typeText(reminder.title),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.reminderDescription)).perform(
            ViewActions.typeText(reminder.description),
            ViewActions.closeSoftKeyboard()
        )

        // choosing location
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_action)).perform(click())
        onView(withId(R.id.map_fragment)).perform(ViewActions.longClick())

        //then saving the reminder
        onView(withId(R.id.save_btn)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        // then checking whether a snack bar was shown or not
        onView(ViewMatchers.withText(R.string.reminder_saved)).inRoot(
            RootMatchers.withDecorView(
                IsNot.not(Matchers.`is`(getActivity(activityScenario)?.window?.decorView))
            )
        )
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )

        //Closing activity
        activityScenario.close()
    }

    // registerIdlingResource
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    //unregisterIdlingResource
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // get activity context
    private fun getActivity(scenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        scenario.onActivity {
            activity = it
        }
        return activity
    }

}
