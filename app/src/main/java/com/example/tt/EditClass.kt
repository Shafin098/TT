package com.example.tt

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_class.*
import java.io.BufferedReader

class EditClass : AppCompatActivity() {

    // don't change file name ever
    private val JSON_FILE_NAME = "schedule_data.json"
    private var classMap = mutableMapOf<String,String>()
    private lateinit var daySelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        classMap = mutableMapOf<String,String>(
            "subject" to intent.getStringExtra("subject"),
            "at" to intent.getStringExtra("at"),
            "by" to intent.getStringExtra("by"),
            "startHour" to intent.getStringExtra("startHour"),
            "startMinute" to intent.getStringExtra("startMinute"),
            "startAmOrPm" to intent.getStringExtra("startAmOrPm"),
            "endHour" to intent.getStringExtra("endHour"),
            "endMinute" to intent.getStringExtra("endMinute"),
            "endAmOrPm" to intent.getStringExtra("endAmOrPm")
        )

        daySelected = intent.getStringExtra("day")

        subject_edit.setText(classMap["subject"], TextView.BufferType.EDITABLE);
        at_edit.setText(classMap["at"], TextView.BufferType.EDITABLE)
        by_edit.setText(classMap["by"], TextView.BufferType.EDITABLE)

        edit_done_btn.setOnClickListener{
            setResult(Activity.RESULT_OK)
            updateClass()
            finish()
        }
    }

    private fun updateClass() {
        val schedule = getSchedule()
        val classToUpdate = schedule.getAllClasses(daySelected).filter { it.equals(classMap) }

        val updatedClass = mutableMapOf<String,String>()
        for ((key,value) in classMap) {
            updatedClass[key] = value
        }
        updatedClass["subject"] = subject_edit.text.toString()
        updatedClass["at"] = at_edit.text.toString()
        updatedClass["by"] = by_edit.text.toString()
        schedule.editClass(daySelected, classToUpdate[0], updatedClass)
        // writing updated info to json data file
        val scheduleDataFile = openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE)
        scheduleDataFile.write(Gson().toJson(schedule).toByteArray())
        scheduleDataFile.close()
    }

    // Creates Schedule class object from json string
    private fun getSchedule(): Schedule {
        val scheduleDataFile = openFileInput(JSON_FILE_NAME).bufferedReader()
        // extracting all Strings from json data file
        val jsonDataString = scheduleDataFile.use(BufferedReader::readText)
        val schedule = Gson().fromJson(jsonDataString, Schedule::class.java)
        return schedule
    }

}
