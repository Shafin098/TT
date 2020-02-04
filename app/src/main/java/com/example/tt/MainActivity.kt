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

    private val NO_CLASS: String = "______"
    // don't change file name
    // because duplicate on AddClass.kt
    // duplicate on EditOrDelete.kt
    private val JSON_FILE_NAME = "schedule_data.json"
    private lateinit var  updatViewsThread: Runnable
    private lateinit var daySelected: String

    private var currentClass: Map<String, String> = getDummyClassMap()

    private var nextClass: Map<String, String> = getDummyClassMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startClassChangeListener()
        setTodaysDay()
        addListenerToDaysTv()

        if (jsonDataFileExists()) {
            // schedule file exists show data
            // open's json data file reads
            // and updates all views that gets data from schedule class
            checkAndUpdateClass()
            addClasssesToHorizontalScroll()
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
        addClasssesToHorizontalScroll()
        checkAndUpdateClass()
    }

    private fun jsonDataFileExists(): Boolean {
        val scheduleFileList = fileList().filter { it == JSON_FILE_NAME  }
        return scheduleFileList.isNotEmpty()
    }

    /**
     * Update all views which gets data from Schedule class
     * Recreate class cards and notes
     */
    private fun addClasssesToHorizontalScroll() {
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

    /**
     * Updates all text in class card
     * and sets onClickListener for EditOrDelete activity
     */
    private fun updateClassCard(classIndex: Int, singleClass: MutableMap<String, String>) {
        val classCard = classes_container.getChildAt(classIndex)
        classCard
            .findViewById<TextView>(R.id.subject_card)?.setText(singleClass["subject"])
        classCard
            .findViewById<TextView>(R.id.start_card)?.setText(getStartTimeOf(singleClass))
        classCard
            .findViewById<TextView>(R.id.at_card)?.setText(singleClass["at"])
        classCard
            .findViewById<TextView>(R.id.end_card)?.setText(getEndTimeOf(singleClass))
        classCard
            .findViewById<TextView>(R.id.by_card)?.setText(singleClass["by"])

        classCard.setOnClickListener {
            val editOrDelIntent = Intent(this, EditOrDeleteClass::class.java)
            editOrDelIntent.putExtra("day", daySelected)
            for ((key,value) in singleClass) {
                editOrDelIntent.putExtra(key, value)
            }
            startActivity(editOrDelIntent)
        }
    }

    private fun getStartTimeOf(singleClass: Map<String, String>): String {
        val startHour =
            formatHourToTweleveHour(singleClass["startHour"]!!, singleClass["startAmOrPm"]!!)
        var startMinute = singleClass["startMinute"]
        if (startMinute!!.toInt() <= 9) {
            startMinute = "0${startMinute}"
        }
        return "${startHour}:${startMinute} ${singleClass["startAmOrPm"]}"
    }

    private fun getEndTimeOf(singleClass: Map<String, String>): String {
        val endHour = formatHourToTweleveHour(singleClass["endHour"]!!, singleClass["endAmOrPm"]!!)
        var endMinute = singleClass["endMinute"]
        if (endMinute!!.toInt() <= 9) {
            endMinute = "0${endMinute}"
        }
        return "${endHour}:${endMinute} ${singleClass["endAmOrPm"]}"
    }

    private fun formatHourToTweleveHour(hour: String, amOrPm: String): String {
        var formattedHour = hour
        if (amOrPm == "pm" && hour.toInt() > 12) {
            formattedHour = (hour.toInt() - 12).toString()
        }
        if (amOrPm == "am" && hour == "0") {
            formattedHour = "12"
        }
        if (formattedHour.toInt() <= 9) {
            formattedHour = "0${formattedHour}"
        }
        return  formattedHour
    }

    private fun getSchedule(): Schedule {
        if (File(filesDir, JSON_FILE_NAME).exists()) {
            val scheduleDataFile = openFileInput(JSON_FILE_NAME).bufferedReader()
            // extracting all Strings from json data file
            val jsonDataString = scheduleDataFile.use(BufferedReader::readText)
            val schedule = Gson().fromJson(jsonDataString, Schedule::class.java)
            return schedule
        }
        return Schedule()
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
        daySelected = getTodaysDay()
        selectDay(daySelected)
    }

    private fun getTodaysDay(): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val todaysDay = calendar.get(Calendar.DAY_OF_WEEK)
        return when (todaysDay) {
            Calendar.SATURDAY -> "sat"
            Calendar.SUNDAY -> "sun"
            Calendar.MONDAY -> "mon"
            Calendar.TUESDAY -> "tue"
            Calendar.WEDNESDAY -> "wed"
            Calendar.THURSDAY -> "thu"
            else -> "fri"
        }
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
        addClasssesToHorizontalScroll()
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
        val currentClassDisplayed =
            findViewById<TextView>(R.id.current_subject_header).text.toString()
        val nextClassDisplayed =
            findViewById<TextView>(R.id.next_subject_header).text.toString()


        if (currentClassDisplayed != currentClass["subject"] || nextClassDisplayed != nextClass["subject"]) {
            updateHeaderViews(currentClass)
        }
    }

    private fun updateHeaderViews(currentClass: Map<String, String>) {
        findViewById<TextView>(R.id.current_subject_header).text = currentClass["subject"]
        findViewById<TextView>(R.id.current_class_room_header).text = currentClass["at"]
        findViewById<TextView>(R.id.time_remaining_header).text = timeDifference(currentClass)

        findViewById<TextView>(R.id.next_subject_header).text = nextClass["subject"]
        findViewById<TextView>(R.id.next_class_room_header).text = nextClass["at"]

    }

    private fun timeDifference(anyClass: Map<String, String>): String {
        if (anyClass["subject"] == NO_CLASS) {
            return "${NO_CLASS}  ${NO_CLASS} "
        }

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

    /**
     * Returns current class
     * and updates next class if class exists
     */
    private fun getCurrentClass(): Map<String,String> {
        val allClasses = getSchedule().getAllClasses(getTodaysDay())
        for ((index, value) in allClasses.withIndex()) {
            var singleClass = allClasses[index]
            if(isCurrentTimeBetweenClassTime(singleClass)) {
                currentClass = singleClass
                if (allClasses.size - 1 >= index + 1) {
                    nextClass = allClasses[index + 1]
                } else {
                    nextClass = getDummyClassMap()
                }
                return singleClass
            } else {
                nextClass = getNextAvailableClass()
            }
        }
        return getDummyClassMap()
    }

    private fun getNextAvailableClass(): Map<String, String> {
        val cal = Calendar.getInstance()
        val currentHour = SimpleDateFormat("H").format(cal.getTime()).toInt()
        val currentMinute = SimpleDateFormat("m").format(cal.getTime()).toInt()

        val allClasses = getSchedule().getAllClasses(getTodaysDay())
        for (singleClass in allClasses) {
            val classStartHour = singleClass["startHour"]!!.toInt()
            val classStartMinute = singleClass["startMinute"]!!.toInt()
            if (classStartHour > currentHour) {
                return  singleClass
            } else if (classStartHour == currentHour) {
                if (classStartMinute > currentMinute) {
                    return singleClass
                }
            }
        }
        return getDummyClassMap()
    }

    private fun getDummyClassMap(): Map<String, String> {
        return hashMapOf(
            "subject" to NO_CLASS,
            "at" to NO_CLASS,
            "by" to NO_CLASS,
            "startHour" to NO_CLASS,
            "startMinute" to NO_CLASS,
            "startAmOrPm" to NO_CLASS,
            "endHour" to NO_CLASS,
            "endMinute" to NO_CLASS,
            "endAmOrPm" to NO_CLASS)
    }

    private fun isCurrentTimeBetweenClassTime(singleClass: Map<String, String>): Boolean {
        val cal = Calendar.getInstance()
        val currentHour = SimpleDateFormat("H").format(cal.getTime()).toInt()
        val currentMinute = SimpleDateFormat("m").format(cal.getTime()).toInt()

        val classStartHour = singleClass["startHour"]!!.toInt()
        val classStartMinute = singleClass["startMinute"]!!.toInt()
        val classEndHour = singleClass["endHour"]!!.toInt()
        val classEndMinute = singleClass["endMinute"]!!.toInt()

        if (currentHour >= classStartHour && currentHour <= classEndHour) {
            if (currentHour == classStartHour) {
                if (currentMinute >= classStartMinute && currentMinute <= classEndMinute) {
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
