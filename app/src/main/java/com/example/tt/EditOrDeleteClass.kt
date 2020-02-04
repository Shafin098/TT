package com.example.tt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_or_delete_class.*
import java.io.BufferedReader

class EditOrDeleteClass : AppCompatActivity() {

    private val EDIT_COMPLETE_CODE: Int = 666
    // don't change file name
    // duplicate on MainActivity.kt and AddClass.kt
    // duplicate on AddClass.kt
    private val JSON_FILE_NAME = "schedule_data.json"
    private lateinit var classMap: Map<String,String>
    private lateinit var daySelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_or_delete_class)

        classMap = hashMapOf<String,String>(
            "subject" to intent.getStringExtra("subject"),
            "at" to intent.getStringExtra("at"),
            "by" to intent.getStringExtra("by"),
            "startHour" to intent.getStringExtra("startHour"),
            "startMinute" to intent.getStringExtra("startMinute"),
            "startAmOrPm" to intent.getStringExtra("startAmOrPm"),
            "endHour" to intent.getStringExtra("endHour"),
            "endMinute" to intent.getStringExtra("endMinute"),
            "endAmOrPm" to intent.getStringExtra("endAmOrPm"))
        daySelected = intent.getStringExtra("day")

        updateClassText()
        delete_class_btn.setOnClickListener{
            deleteClass()
        }
        edit_class_btn.setOnClickListener{
            editClass()
        }
    }

    private fun updateClassText() {
        subject_edit_or_delete.text = intent.getStringExtra("subject")
        at_edit_or_delete.text = intent.getStringExtra("at")
        start_time_edit_or_delete.text = "${intent.getStringExtra("startHour")}:" +
                "${intent.getStringExtra("startMinute")} ${intent.getStringExtra("startAmOrPm")}"
        end_time_edit_or_delete.text = "${intent.getStringExtra("endHour")}:" +
                "${intent.getStringExtra("endMinute")} ${intent.getStringExtra("endAmOrPm")}"
    }

    private fun deleteClass() {
        // Getting all class schedule data from storage
        val schedule = getSchedule(daySelected)
        schedule.deleteClass(daySelected, classMap)
        // Writing new class schedule data from storage
        val scheduleDataFile = openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE)
        scheduleDataFile.write(Gson().toJson(schedule).toByteArray())
        scheduleDataFile.close()
        finish()
    }

    private fun editClass() {
        val editIntent = Intent(this, EditClass::class.java)
        editIntent.putExtra("day", daySelected)
        for ((key,value) in classMap) {
            editIntent.putExtra(key, value)
        }
        startActivityForResult(editIntent, EDIT_COMPLETE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_COMPLETE_CODE) {
            finish()
        }
    }

    private fun getSchedule(daySelected: String?): Schedule {
        val scheduleDataFile = openFileInput(JSON_FILE_NAME).bufferedReader()
        // extracting all Strings from json data file
        val jsonDataString = scheduleDataFile.use(BufferedReader::readText)
        val schedule = Gson().fromJson(jsonDataString, Schedule::class.java)
        return schedule
    }
}
