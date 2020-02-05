package com.example.tt

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_class.*
import kotlinx.android.synthetic.main.clock_input.*
import java.io.BufferedReader


class AddClass : AppCompatActivity() {

    // don't change file name
    // duplicate on MainActivity.kt
    // duplicate on EditOrDelete.kt
    private val JSON_FILE_NAME = "schedule_data.json"
    // 1 when only subject, room and teacher's name input showing
    // 2 starts time input showing
    // 3 ends time input showing
    private var inputStage = 1
    // will be passed in intent 'day'
    private lateinit var daySelected: String

    private lateinit var subject: String
    private lateinit var at: String
    private lateinit var by: String
    private lateinit var startHour: String
    private lateinit var startMinute: String
    private lateinit var startAmOrPm: String
    private lateinit var endHour: String
    private lateinit var endMinute: String
    private lateinit var endAmOrPm: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_class)

        daySelected = intent.getStringExtra("day").toString()

        edit_done_btn.setOnClickListener { onInputSubmit(it) }
    }

    private fun createClassHashMap(): Map<String,String> {
        val classMap = hashMapOf(
            "subject" to subject,
            "at" to at,
            "by" to by,
            "startHour" to startHour,
            "startMinute" to startMinute,
            "startAmOrPm" to startAmOrPm,
            "endHour" to endHour,
            "endMinute" to endMinute,
            "endAmOrPm" to endAmOrPm)
        // adding new class to json data file
        writeToStorage(classMap)
        return classMap
    }

    // adds new class to json data file
    private fun writeToStorage(classMap: HashMap<String, String>) {
        val schedule: Schedule = getSchedule()
        schedule.addNewClass(daySelected, classMap)
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

    private fun onInputSubmit(btn: View) {
        //Toast.makeText(this, daySelected, Toast.LENGTH_LONG).show()
        if (inputStage == 1) {
            if (subject_tv.text.isNotEmpty()) {
                subject = subject_tv.text.toString()
                at = at_tv.text.toString()
                by = by_tv.text.toString()

                inputStage++
                outer_container.removeAllViews()
                // adding time picker to views
                layoutInflater.inflate(R.layout.clock_input, outer_container)
                clock_text.text = "Starting time"
            } else {
                Toast.makeText(this, "Type subject's name", Toast.LENGTH_LONG).show()
            }
        } else if (inputStage == 2) {
            val timePicker = findViewById<TimePicker>(R.id.time_picker)
            startHour = timePicker.hour.toString()
            startMinute = timePicker.minute.toString()
            if (startHour.toInt() >= 12) {
                startAmOrPm = "pm"
            } else {
                startAmOrPm = "am"
            }
            startMinute = timePicker.minute.toString()

            inputStage++
            outer_container.removeAllViews()
            edit_done_btn.text = "create new class"
            // adding time picker to views
            layoutInflater.inflate(R.layout.clock_input, outer_container)
            findViewById<TextView>(R.id.clock_text).setText("Ending time")
        } else if (inputStage == 3){
            val timePicker = findViewById<TimePicker>(R.id.time_picker)
            endHour = timePicker.hour.toString()
            endMinute = timePicker.minute.toString()
            if (startHour.toInt() == endHour.toInt()) {
                if (startMinute.toInt() > endMinute.toInt()) {
                    Toast.makeText(this, "Invalid time", Toast.LENGTH_LONG).show()
                } else {
                    if (endHour.toInt() >= 12) {
                        endAmOrPm = "pm"
                    } else {
                        endAmOrPm = "am"
                    }
                    endMinute = timePicker.minute.toString()
                    val classMap = createClassHashMap()
                    Log.d("input", classMap.toString())
                    finish()
                }
            } else if (endHour.toInt() < startHour.toInt()) {
                Toast.makeText(this, "Invalid time", Toast.LENGTH_LONG).show()
            } else {
                val timePicker = findViewById<TimePicker>(R.id.time_picker)
                if (endHour.toInt() >= 12) {
                    endAmOrPm = "pm"
                } else {
                    endAmOrPm = "am"
                }
                endMinute = timePicker.minute.toString()
                val classMap = createClassHashMap()
                Log.d("input", classMap.toString())
                finish()
            }
        }
    }

} // class ends here
