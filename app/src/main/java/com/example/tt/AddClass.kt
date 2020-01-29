package com.example.tt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_class.*

class AddClass : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_class)
        add_class_btn.setOnClickListener {
           val scheduleMap = createScheduleHashMap()
           finish()
        }
        Toast.makeText(this, intent.getStringExtra("day").toString(), Toast.LENGTH_LONG).show()
    }

    private fun createScheduleHashMap(): Map<String,ArrayList<Map<String,String>>> {
        val scheduleMap = HashMap<String,ArrayList<Map<String,String>>>()
        val days = listOf<String>("sat", "sun", "mon", "tue", "wed", "thu", "fri")
        for (day in days) {
            scheduleMap.put(day, ArrayList<Map<String,String>>())
        }
        return scheduleMap
    }
}
