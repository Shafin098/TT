package com.example.tt

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_note.*
import java.io.BufferedReader
import java.io.File

class CreateNote : AppCompatActivity() {

    private val NOTES_FILE_NAME = "notes.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        note_done_btn.setOnClickListener {
            if (notesDataFileExists()) {
                val allNotes: Notes = getNotes()
                allNotes.addNote(getCurrentNote())
                writeToStorage(allNotes)
            } else {
                // app first opened
                // schedule file doesn't exits create new fle
                File(filesDir, NOTES_FILE_NAME).createNewFile()
                val notes: Notes = Notes()
                notes.addNote(getCurrentNote())
                writeToStorage(notes)
            }
            finish()
        }
    }

    private fun writeToStorage(notes: Notes) {
        val gson = Gson()
        val notesDataFile = openFileOutput(NOTES_FILE_NAME, Context.MODE_PRIVATE)
        notesDataFile.write(gson.toJson(notes).toByteArray())
        notesDataFile.close()
    }

    private fun getNotes(): Notes {
        val scheduleDataFile = openFileInput(NOTES_FILE_NAME).bufferedReader()
        // extracting all Strings from json data file
        val jsonDataString = scheduleDataFile.use(BufferedReader::readText)
        val notes = Gson().fromJson(jsonDataString, Notes::class.java)
        return notes
    }

    private fun getCurrentNote(): Map<String,String> {
        return mapOf(
            "content" to note_content.text.toString(),
            "year" to note_expire_date.year.toString(),
            "month" to note_expire_date.month.toString(),
            "dayOfTheMonth" to note_expire_date.dayOfMonth.toString()
        )
    }

    private fun notesDataFileExists(): Boolean {
        val scheduleFileList = fileList().filter { it == NOTES_FILE_NAME  }
        return scheduleFileList.isNotEmpty()
    }
}
