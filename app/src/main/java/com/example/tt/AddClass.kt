package com.example.tt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_class.*
import kotlinx.android.synthetic.main.clock_input.*
import android.util.Log


class AddClass : AppCompatActivity() {

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

        next_btn.setOnClickListener { onInputSubmit(it) }
        //debug remove later
        Toast.makeText(this, daySelected, Toast.LENGTH_LONG).show()
    }

    private fun createClassHashMap(): Map<String,String> {
//        val scheduleMap = HashMap<String,ArrayList<Map<String,String>>>()
//        val days = listOf<String>("sat", "sun", "mon", "tue", "wed", "thu", "fri")
//        for (day in days) {
//            scheduleMap.put(day, ArrayList<Map<String,String>>())
//        }
        val classMap = hashMapOf<String,String>(
            "subject" to subject,
            "at" to at,
            "by" to by,
            "startHour" to startHour,
            "startMinute" to startMinute,
            "startAmOrPm" to startAmOrPm,
            "endHour" to endHour,
            "endMinute" to endMinute,
            "endAmOrPm" to endAmOrPm)
        return classMap
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
            startHour = time_picker.hour.toString()
            if (startHour.toInt() >= 12) {
                startAmOrPm = "pm"
            } else {
                startAmOrPm = "am"
            }
            startMinute = time_picker.minute.toString()

            inputStage++
            outer_container.removeAllViews()
            next_btn.text = "create new class"
            // adding time picker to views
            layoutInflater.inflate(R.layout.clock_input, outer_container)
            findViewById<TextView>(R.id.clock_text).setText("Ending time")
        } else if (inputStage == 3){
            endHour = time_picker.hour.toString()
            if (endHour.toInt() >= 12) {
               endAmOrPm = "pm"
            } else {
                endAmOrPm = "am"
            }
            endMinute = time_picker.minute.toString()
            val classMap = createClassHashMap()
            Log.d("input", classMap.toString())
            finish()
        }
    }

} // class ends here
