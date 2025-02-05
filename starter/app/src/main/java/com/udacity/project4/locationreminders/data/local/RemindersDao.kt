package com.udacity.project4.locationreminders.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

/**
 * Data Access Object for the reminders table.
 */
@Dao
interface RemindersDao {

    @Query("SELECT * FROM reminders")
    suspend fun getReminders(): List<ReminderDTO>

    @Query("SELECT * FROM reminders where entry_id = :reminderId")
    suspend fun getReminderById(reminderId: String): ReminderDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: ReminderDTO)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()

    //delete specific reminder
    @Query("DELETE FROM reminders where entry_id = :reminderId")
    suspend fun deleteReminderById(reminderId: String)
}