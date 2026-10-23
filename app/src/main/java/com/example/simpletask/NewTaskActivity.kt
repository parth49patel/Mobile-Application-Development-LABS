package com.example.simpletask

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class NewTaskActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao

    // Default color hex (Yellow)
    private var selectedColour: String = "#FFF9C4"
    private lateinit var colorSquares: List<Pair<View, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        taskDao = TaskDao(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etTaskTitle)
        val etDeadline = findViewById<EditText>(R.id.etDeadline)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnDone = findViewById<Button>(R.id.btnDone)

        // Find each square view
        val colorYellow = findViewById<View>(R.id.colorYellow)
        val colorOrange = findViewById<View>(R.id.colorOrange)
        val colorBlue = findViewById<View>(R.id.colorBlue)
        val colorGreen = findViewById<View>(R.id.colorGreen)
        val colorPurple = findViewById<View>(R.id.colorPurple)

        // Pair each view with its respective Hex color code
        colorSquares = listOf(
            Pair(colorYellow, "#FFF9C4"),
            Pair(colorOrange, "#FFE0B2"),
            Pair(colorBlue, "#BBDEFB"),
            Pair(colorGreen, "#C8E6C9"),
            Pair(colorPurple, "#E1BEE7")
        )

        // Set up click listeners for each color square
        for ((view, hex) in colorSquares) {
            view.setOnClickListener {
                selectedColour = hex
                refreshColorSquareBorders()
            }
        }

        // Draw initial colors and default selection border
        refreshColorSquareBorders()

        // Deadline DatePicker dialog
        etDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    etDeadline.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Back button: discard changes and return without recreating MainActivity
        btnBack.setOnClickListener {
            finish()
        }

        // Done button logic
        btnDone.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isEmpty()) {
                // If title is empty, show error message, do not save, and stay on screen
                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                // Save task to SQLite via DAO
                val newTask = Task(
                    title = title,
                    deadline = deadline,
                    description = description,
                    colour = selectedColour
                )
                taskDao.insertTask(newTask)

                Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show()
                finish() // Returns to existing MainActivity without recreating it
            }
        }
    }

    private fun refreshColorSquareBorders() {
        for ((view, hex) in colorSquares) {
            val isSelected = (hex == selectedColour)

            // Create a custom drawable programmatically for each square
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f
                setColor(Color.parseColor(hex))
                if (isSelected) {
                    setStroke(8, Color.parseColor("#212121"))
                } else {
                    setStroke(2, Color.parseColor("#BDBDBD"))
                }
            }
            view.background = drawable
        }
    }
}