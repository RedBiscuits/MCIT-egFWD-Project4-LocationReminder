package com.udacity.project4.locationreminders.navigationtest

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.EspressoIdlingResource
import com.udacity.project4.R
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
class NavigationTest :
    AutoCloseKoinTest() {

    //data source, idle resources and context initializations
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private val reminder = ReminderDTO("reminder", "description", "location", 12.0, 12.0,"1")


    //before starting any test case it does the following
    @Before
    fun init() {

        //stop the original app koin
        stopKoin()

        // gets app context
        appContext = ApplicationProvider.getApplicationContext()

        //gets view models and data sources by dependency injection
        val myModule = module {

            //reminders list view model by DI
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            //save reminders view model by DI
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            // repository and database by DI
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


    // main coroutine
    @ExperimentalCoroutinesApi
    @Test
    fun showReminderSavedToast() = runBlocking{

        // Given - Activity exists with scenario
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

                /* When - Navigating the app */

        // navigating from and to addReminder fragment
        onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())
        pressBack()
        onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())

        // choosing location
        onView(ViewMatchers.withId(R.id.selectLocation)).perform(click())
        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_action)).perform(click())
        onView(ViewMatchers.withId(R.id.map_fragment)).perform(ViewActions.longClick())

        // saving the reminder
        onView(ViewMatchers.withId(R.id.save_btn)).perform(click())
        onView(ViewMatchers.withId(R.id.saveReminder)).perform(click())

        // saving reminder info to check save button
        onView(ViewMatchers.withId(R.id.reminderTitle)).perform(
            ViewActions.typeText(reminder.title),
            ViewActions.closeSoftKeyboard()
        )
        onView(ViewMatchers.withId(R.id.reminderDescription)).perform(
            ViewActions.typeText(reminder.description),
            ViewActions.closeSoftKeyboard()
        )
        // saving the reminder
        onView(ViewMatchers.withId(R.id.saveReminder)).perform(click())

        //Then - app works as asked
        activityScenario.close()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

//                      CANT ACCESS TOOLBAR
//
//    fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
//            : String {
//        var description = ""
//        onActivity {
//            description =
//                it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
//        }
//        return description
//    }
}
