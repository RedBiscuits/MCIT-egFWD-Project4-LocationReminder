package com.udacity.project4.locationreminders


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import org.koin.android.ext.android.inject


class ReminderDescriptionActivity : AppCompatActivity() {

    val viewModel: ReminderDescriptionViewModel by inject()

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.reminderDataItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem?

        binding.deleteBtn.setOnClickListener{
            viewModel.deleteReminder(binding.reminderDataItem!!)
            finish()
        }

        binding.editButton.setOnClickListener{

            val intent = Intent(this, RemindersActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("reminderId", binding.reminderDataItem!!)
            startActivity(intent)
        }

    }
}
