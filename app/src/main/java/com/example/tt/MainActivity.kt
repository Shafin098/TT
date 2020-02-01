package com.example.tt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val NO_CLASS: String = "__no__class"
    // don't change file name
    // because duplicate on AddClass.kt
    private val JSON_FILE_NAME = "schedule_data.json"
    private lateinit var  updatViewsThread: Runnable
    private lateinit var daySelected: String
    private var currentClass: Map<String,String> = mapOf(
        "subject" to NO_CLASS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startClassChangeListener()
        setTodaysDay()
        addListenerToDaysTv()

        if (jsonDataFileExists() && anyClassScheduleToday()) {
            // schedule file exists show data
            // open's json data file reads
            // and updates all views that gets data from schedule class
            checkAndUpdateClass()
            updateViews()
        } else {
            // app first opened
            // schedule file doesn't exits create new fle
            File(filesDir, JSON_FILE_NAME).createNewFile()
            val schedule: Schedule = Schedule()
            val gson = Gson()
            val scheduleDataFile = openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE)
            scheduleDataFile.write(gson.toJson(schedule).toByteArray())
            scheduleDataFile.close()
        }
    }

    override fun onResume() {
        super.onResume()
        // updates class cards in horizontal scroll
        updateViews()
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
        inflateAddClassCard()
    }

    private fun updateClassCard(classIndex: Int, schedule: MutableMap<String, String>) {
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.subject_card)?.setText(schedule["subject"])
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.start_card)?.setText(getStartTimeOf(schedule))
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.at_card)?.setText(schedule["at"])
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.end_card)?.setText(getEndTimeOf(schedule))
        classes_container.getChildAt(classIndex)
            .findViewById<TextView>(R.id.by_card)?.setText(schedule["by"])
    }

    private fun getStartTimeOf(singleClass: Map<String, String>): String {
        val startHour =
            formatHourToTweleveHour(singleClass["startHour"]!!, singleClass["startAmOrPm"]!!)
        return "${startHour}:${singleClass["startMinute"]} ${singleClass["startAmOrPm"]}"
    }

    private fun getEndTimeOf(singleClass: Map<String, String>): String {
        val endHour = formatHourToTweleveHour(singleClass["endHour"]!!, singleClass["endAmOrPm"]!!)
        return "${endHour}:${singleClass["endMinute"]} ${singleClass["endAmOrPm"]}"
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

    private fun getSchedule(): Schedule {
        val scheduleDataFile = openFileInput(JSON_FILE_NAME).bufferedReader()
        // extracting all Strings from json data file
        val jsonDataString = scheduleDataFile.use(BufferedReader::readText)
        val schedule = Gson().fromJson(jsonDataString, Schedule::class.java)
        return schedule
    }

    private fun inflateAddClassCard() {
       val addCard = layoutInflater.inflate(R.layout.class_card_add, classes_container)
        addCard.findViewById<Button>(R.id.add_class_btn_card).setOnClickListener {
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
        val days = listOf("sat", "sun", "mon", "tue", "wed", "thu", "fri")
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
        // gets classes on selected day
        // and updates all cards horizontal scroll
        updateViews()
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
    private fun startClassChangeListener() {
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
        if (currentClass["subject"] != NO_CLASS) {
            if (current_subject_header.text.toString() != currentClass["subject"]) {
                updateHeaderViews(currentClass)
            }
        }
    }

    private fun updateHeaderViews(currentClass: Map<String, String>) {
        // TODO add next subject
        findViewById<TextView>(R.id.current_subject_header).text = currentClass["subject"]
        findViewById<TextView>(R.id.current_class_room_header).text = currentClass["at"]
        findViewById<TextView>(R.id.time_remaining_header).text = timeDifference(currentClass)
    }

    private fun timeDifference(anyClass: Map<String, String>): String {
        val cal = Calendar.getInstance()
        var hourDifference = 0
        var minuteDifference = 1

        val endHour = anyClass["endHour"]!!.toInt()
        val endMinute = anyClass["endMinute"]!!.toInt()

        var currentHour = SimpleDateFormat("H").format(cal.getTime()).toInt()
        var currentMinute = SimpleDateFormat("m").format(cal.getTime()).toInt()

        while (currentMinute < endMinute || currentHour < endHour) {
            currentMinute++
            if (currentMinute % 60 == 0) {
                currentHour++
                currentMinute = 0
            }
            minuteDifference++
        }
        minuteDifference--
        hourDifference = minuteDifference / 60
        minuteDifference = minuteDifference - (hourDifference * 60)
        if (hourDifference > 0) {
            return "${hourDifference} hour ${minuteDifference} minute"
        } else {
            return "${minuteDifference} minute"
        }
    }

    private fun getCurrentClass(): Map<String,String> {
        val allClasses = getSchedule().getAllClasses(daySelected)
        for (singleClass in allClasses) {
            if(isCurrentTimeBetweenClassTime(singleClass)) {
                currentClass = singleClass
                return singleClass
            }
        }
        return mapOf("subject" to NO_CLASS)
    }

    private fun isCurrentTimeBetweenClassTime(singleClass: Map<String, String>): Boolean {
        val cal = Calendar.getInstance()
        val currentHour = SimpleDateFormat("H").format(cal.getTime()).toInt()
        val currentMinute = SimpleDateFormat("m").format(cal.getTime()).toInt()

        val classStartHour = singleClass["startHour"]!!.toInt()
        val classStartMinute = singleClass["startMinute"]!!.toInt()
        val classEndHour = singleClass["endHour"]!!.toInt()
        val classEndMinute = singleClass["endMinute"]!!.toInt()
        // TODO start from here
        if (currentHour >= classStartHour && currentHour <= classEndHour) {
            if (currentHour == classStartHour) {
                if (currentMinute >= classStartMinute) {
                    return true
                } else {
                    return false
                }
            }
            if (currentHour == classEndHour) {
                if (currentMinute <= classEndMinute) {
                    return true
                } else {
                    return false
                }
            }
            return true
        } else {
            return false
        }
    }

}
