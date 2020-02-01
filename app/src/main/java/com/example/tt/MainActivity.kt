package com.example.tt

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.class_card_add.*
import java.io.File
import java.util.*
import android.view.View
import com.google.gson.Gson
import java.io.BufferedReader


class MainActivity : AppCompatActivity() {

    // don't change file name
    // because duplicate on AddClass.kt
    private val JSON_FILE_NAME = "schedule_data.json"
    private lateinit var  updatViewsThread: Runnable
    private lateinit var daySelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        classChangeListener()
        setTodaysDay()
        addListenerToDaysTv()

        if (jsonDataFileExists() && anyClassScheduleToday()) {
            // schedule file exists show data
            // open's json data file reads
            // and updates all views that gets data from schedu;e class
            updateViews()
        } else {
            // schedule file doesn't exits create new fle
            File(filesDir, JSON_FILE_NAME).createNewFile()
            val schedule: Schedule = Schedule()
            val gson = Gson()
            val scheduleDataFile = openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE)
            scheduleDataFile.write(gson.toJson(schedule).toByteArray())
//            Log.d("json", gson.toJson(schedule))
            scheduleDataFile.close()

            // first app opened or data file not created
            // so subject room time ramaining data is not available
            time_remaining_header.text = "_________"
            next_subject_header.text = "_______"
            next_class_room_header.text = "___"
        }
        //should be at bottom don't remove
        inflateAddClassCard()
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdateClass()
        Toast.makeText(this, "resumed", Toast.LENGTH_LONG).show()
    }

    private fun jsonDataFileExists(): Boolean {
        val scheduleFileList = fileList().filter { it == JSON_FILE_NAME  }
        return scheduleFileList.isNotEmpty()
    }

    private fun anyClassScheduleToday(): Boolean {
        val anyClassToday = if (getSchedule().getAllClasses(daySelected).size > 0) true else false
        return anyClassToday

    }

    /**
     * Update all views which gets data from Schedule class
     * Recreate class cards and notes
     */
    private fun updateViews() {
        classes_container.removeAllViews()

        val schedule: Schedule = getSchedule()
        var currentClassIndex = 0
        for (singleClass in schedule.getAllClasses(daySelected)) {
            layoutInflater.inflate(R.layout.class_card, classes_container)
            updateClassCard(currentClassIndex, singleClass)
            currentClassIndex++
        }
    }

    private fun updateClassCard(classIndex: Int, schedule: MutableMap<String, String>) {
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.subject_card)?.setText(schedule.get("subject"))
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.start_card)?.setText(getStartTime(schedule))
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.at_card)?.setText(schedule.get("at"))
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.end_card)?.setText(getEndTime(schedule))
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.by_card)?.setText(schedule.get("by"))
    }

    private fun getStartTime(schedule: MutableMap<String, String>): String {
        val startHour = formatHourToTweleveHour(schedule.get("startHour")!!, schedule.get("startAmOrPm")!!)
        return "${startHour}:${schedule.get("startMinute")} ${schedule.get("startAmOrPm")}"
    }

    private fun formatHourToTweleveHour(hour: String, amOrPm: String): String {
        var formattedHour = hour
        if (amOrPm == "pm") {
            formattedHour = (hour.toInt() - 12).toString()
        }
        if (amOrPm == "am" && hour == "0") {
            formattedHour = "12"
        }
        return  formattedHour
    }

    private fun getEndTime(schedule: MutableMap<String, String>): String {
        val endHour = formatHourToTweleveHour(schedule.get("endHour")!!, schedule.get("endAmOrPm")!!)
        return "${endHour}:${schedule.get("endMinute")} ${schedule.get("endAmOrPm")}"
    }

    private fun getSchedule(): Schedule {
        val scheduleDataFile = openFileInput(JSON_FILE_NAME).bufferedReader()
        // extracting all Strings from json data file
        val jsonDataString = scheduleDataFile.use(BufferedReader::readText)
        val schedule = Gson().fromJson(jsonDataString, Schedule::class.java)
        return schedule
    }

    private fun inflateAddClassCard() {
       layoutInflater.inflate(R.layout.class_card_add, classes_container)
        add_class_btn_card.setOnClickListener {
            val intent = Intent(this, AddClass::class.java)
            intent.putExtra("day", daySelected)
            startActivity(intent)
        }
    }

    private fun setTodaysDay() {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val todaysDay = calendar.get(Calendar.DAY_OF_WEEK)
        daySelected = when (todaysDay) {
            Calendar.SATURDAY -> "sat"
            Calendar.SUNDAY -> "sun"
            Calendar.MONDAY -> "mon"
            Calendar.TUESDAY -> "tue"
            Calendar.WEDNESDAY -> "wed"
            Calendar.THURSDAY -> "thu"
            else -> "fri"
        }
        selectDay(daySelected)
    }

    private fun addListenerToDaysTv() {
        val days = listOf<String>("sat", "sun", "mon", "tue", "wed", "thu", "fri")
        for (day in days) {
            val tv = resources.getIdentifier(day, "id", packageName)
            findViewById<TextView>(tv).setOnClickListener {
                onDaySelected(it)
            }
        }
    }

    private fun onDaySelected(dayView: View?) {
        val days = listOf("sat", "sun", "mon", "tue", "wed", "thu", "fri")
        val dayTextView = dayView as TextView?
        for (day in days) {
            if (day == dayTextView?.text.toString()) {
                selectDay(day)
            } else {
                unSelectDay(day)
            }
        }
    }

    private fun selectDay(day: String) {
        daySelected = day
        val textViewId = resources.getIdentifier(day, "id", packageName)
        val selectedTv = findViewById<TextView>(textViewId)
        selectedTv.setTextColor(resources.getColor(R.color.colorPrimary, theme))
        Toast.makeText(this, day, Toast.LENGTH_LONG).show()
    }

    private fun unSelectDay(day: String) {
        val textViewId = resources.getIdentifier(day, "id", packageName)
        findViewById<TextView>(textViewId).setTextColor(resources.getColor(R.color.text, theme))
    }

    /**
     * Checks if current class changed every 15 seconds
     * updates all related views if changes
     */
    private fun classChangeListener() {
        val handler = Handler()
        updatViewsThread = object: Runnable {
            override fun run() {
                checkAndUpdateClass()
                handler.postDelayed(updatViewsThread, 15000)
            }
        }
        handler.post(updatViewsThread)
    }

    private fun checkAndUpdateClass() {
        val currentClass = getCurrentClass()
        if (current_subject_header.text.toString() != currentClass) {
            updateHeaderViews(currentClass)
        }
    }

    private fun updateHeaderViews(currentClass: String) {
        // TODO
        current_subject_header.text = currentClass
    }

    private fun getCurrentClass(): String {
        // TODO
        return "English"
    }

}
