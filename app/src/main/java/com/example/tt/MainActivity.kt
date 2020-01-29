package com.example.tt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.class_card_add.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.view.View


class MainActivity : AppCompatActivity() {

    private val JSON_FILE_NAME = "schedule_data.json"
    private lateinit var  clockThread: Runnable
    private lateinit var daySelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scheduleFileList = fileList().filter { it == JSON_FILE_NAME  }
        if (scheduleFileList.isNotEmpty()) {
            // schedule file exists show data
            layoutInflater.inflate(R.layout.class_card, classes_container)
            layoutInflater.inflate(R.layout.class_card, classes_container)
        } else {
            // schedule file doesn't exits create new fle
            File(filesDir, JSON_FILE_NAME).createNewFile()

            // updating current_time_header TextView every minutes
            val handler = Handler()
            clockThread = object: Runnable {
                 override fun run() {
                    current_time_header.text = SimpleDateFormat("hh:mm a",
                        Locale.getDefault()).format(Date())
                    handler.postDelayed(clockThread, 60000)
                }
            }
            handler.post(clockThread)

            // first app opened or data file not created
            // so subject room time ramaining data is not available
            time_remaining_header.text = "_________"
            next_subject_header.text = "_______"
            next_class_room_header.text = "___"
        }
        inflateAddClassCard()
        setTodaysDay()
        addListenerToDaysTv()
    }

    private fun inflateAddClassCard() {
        layoutInflater.inflate(R.layout.class_card_add, classes_container)
        add_class_card.setOnClickListener {
            val intent = Intent(this, AddClass::class.java)
            intent.putExtra("day", daySelected)
            startActivity(intent)
        }
    }

    private fun setTodaysDay() {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        var todaysDay = calendar.get(Calendar.DAY_OF_WEEK)
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
        val days = listOf<String>("sat", "sun", "mon", "tue", "wed", "thu", "fri")
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

}
