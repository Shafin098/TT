package com.example.tt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_class.*
import kotlinx.android.synthetic.main.clock_input.*

class AddClass : AppCompatActivity() {

    // 1 when only subject, room and teacher's name input showing
    // 2 starts time input showing
    // 3 ends time input showing
    private var inputStage = 1
    // will be passed in intent 'day'
    private lateinit var daySelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_class)

        daySelected = intent.getStringExtra("day").toString()

        next_btn.setOnClickListener { onInputSubmit(it) }
        //debug remove later
        Toast.makeText(this, daySelected, Toast.LENGTH_LONG).show()
    }

    private fun createScheduleHashMap(): Map<String,ArrayList<Map<String,String>>> {
        val scheduleMap = HashMap<String,ArrayList<Map<String,String>>>()
        val days = listOf<String>("sat", "sun", "mon", "tue", "wed", "thu", "fri")
        for (day in days) {
            scheduleMap.put(day, ArrayList<Map<String,String>>())
        }
        return scheduleMap
    }

    private fun onInputSubmit(btn: View) {
        //Toast.makeText(this, daySelected, Toast.LENGTH_LONG).show()
        if (inputStage == 1) {
            inputStage++
            outer_container.removeAllViews()
            addClockViews()
        } else if (inputStage == 2) {
            inputStage++
            outer_container.removeAllViews()
            //addClockViews()
            layoutInflater.inflate(R.layout.clock_input, outer_container)
            clock_text.text = "Starting time"
        } else if (inputStage == 3){
            outer_container.removeAllViews()
            next_btn.text = "create new class"
            // adding time picker to views
            layoutInflater.inflate(R.layout.clock_input, outer_container)
            clock_text.text = "Ending time"
        }
    }

    private fun addClockViews() {
        if (inputStage == 2) {
            layoutInflater.inflate(R.layout.clock_input, outer_container)
            clock_text.text = "Starting time"
        } else if (inputStage == 3) {
            //persist all input
            // call finish()
            next_btn.text = "create new class"
            // adding time picker to views
            layoutInflater.inflate(R.layout.clock_input, outer_container)
            clock_text.text = "Ending time"
        }
    }

} // class ends here
