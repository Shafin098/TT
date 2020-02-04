package com.example.tt

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_class.*

class EditClass : AppCompatActivity() {

    // don't chanmge file name ever
    private val JSON_FILE_NAME = "schedule_data.json"
    private lateinit var classMap: Map<String,String>
    private lateinit var daySelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

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

        subject_edit.setText(classMap["subject"], TextView.BufferType.EDITABLE);
        // TODO start working from here
        // temporay
        next_btn.setOnClickListener{
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

}
