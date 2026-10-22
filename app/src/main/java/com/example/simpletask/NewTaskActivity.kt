package com.example.simpletask

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class NewTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etTaskTitle)
        val etDeadline = findViewById<EditText>(R.id.etDeadline)
        val btnDone = findViewById<Button>(R.id.btnDone)

        // Date Picker dialog for deadline
        etDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    etDeadline.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Back button discards changes and returns without recreating MainActivity
        btnBack.setOnClickListener {
            finish()
        }

        // Done button validation
        btnDone.setOnClickListener {
            val title = etTitle.text.toString().trim()

            if (title.isEmpty()) {
                // If title is empty, show Toast and stay on screen
                Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show()
            } else {
                // If valid, show confirmation Toast and return to Home
                Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show()
                finish() // Calling finish() pops back to existing MainActivity instance
            }
        }
    }
}