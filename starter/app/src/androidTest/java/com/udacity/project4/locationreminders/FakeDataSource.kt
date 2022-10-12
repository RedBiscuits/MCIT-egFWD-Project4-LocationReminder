package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
/*
*  Fake repository for testing purposes
* */

// Fake data class with parametered list of data objects
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :

    // for error testing
    ReminderDataSource {
    private var shouldReturnError = false

    //setter
    fun setShouldReturnError( value:Boolean){
        shouldReturnError = value
    }

    //gets all reminders passed in the constructor earlier
    //if error is true returns error
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        //if error is true
        if(shouldReturnError){
            return Result.Error("Reminders not found")
        }

        //if reminders were passed when initializing
        reminders?.let { return Result.Success(ArrayList(it)) }

        //if reminders are null
        return Result.Error("Reminders not found")

    }

    //adds a reminder to existing reminders
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    //gets reminder by ID
    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        //if it should error returns an error where reminder with specific ID cant be found
        if(shouldReturnError){
            return Result.Error("Reminder not found!")
        }

        //if reminder exists it's returned
        val reminder = reminders?.find {
            it.id == id
        }
        return if (reminder!=null){
            Result.Success(reminder)
        }
        //if it doesnt exist it errors
        else{
            Result.Error("Reminder not found!")
        }
    }

    //clearing all reminders
    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    //delete specific reminder with unique ID
    override suspend fun deleteReminder(id: String) {
        val reminder = reminders?.find {
            it.id == id
        }
        reminders?.remove(reminder)
    }

}