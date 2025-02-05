package com.udacity.project4.locationreminders

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.android.synthetic.main.activity_reminders.*
import org.koin.android.ext.android.inject


class RemindersActivity : AppCompatActivity() {

    val viewModel: SaveReminderViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)
        val reminderDataItem = intent.getSerializableExtra("reminderId") as ReminderDataItem?

        if (reminderDataItem != null){
            viewModel.editReminder(reminderDataItem)
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(ReminderListFragmentDirections.toSaveReminder())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
