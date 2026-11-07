package com.example.simpletask

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.util.Calendar

class NewTaskActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao
    private var editingTaskId: Long = -1L

    private var selectedColour: String = "#FFF9C4"
    private var currentImageBase64: String? = null
    private lateinit var colorSquares: List<Pair<View, String>>

    private lateinit var ivTaskImage: ImageView
    private lateinit var layoutImagePreview: FrameLayout

    // 1. Photo Picker for Gallery
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                processAndSetImage(bitmap)
            }
        }
    }

    // 2. Camera Preview Capture
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            processAndSetImage(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        taskDao = TaskDao(this)
        editingTaskId = intent.getLongExtra("TASK_ID", -1L)

        val tvScreenTitle = findViewById<TextView>(R.id.tvScreenTitle)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etTaskTitle)
        val etDeadline = findViewById<EditText>(R.id.etDeadline)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnDone = findViewById<Button>(R.id.btnDone)
        val btnDeleteTaskScreen = findViewById<Button>(R.id.btnDeleteTaskScreen)

        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val btnCapturePhoto = findViewById<Button>(R.id.btnCapturePhoto)
        val btnRemoveImage = findViewById<ImageButton>(R.id.btnRemoveImage)
        ivTaskImage = findViewById(R.id.ivTaskImage)
        layoutImagePreview = findViewById(R.id.layoutImagePreview)

        // Setup Color Squares
        val colorYellow = findViewById<View>(R.id.colorYellow)
        val colorOrange = findViewById<View>(R.id.colorOrange)
        val colorBlue = findViewById<View>(R.id.colorBlue)
        val colorGreen = findViewById<View>(R.id.colorGreen)
        val colorPurple = findViewById<View>(R.id.colorPurple)

        colorSquares = listOf(
            Pair(colorYellow, "#FFF9C4"),
            Pair(colorOrange, "#FFE0B2"),
            Pair(colorBlue, "#BBDEFB"),
            Pair(colorGreen, "#C8E6C9"),
            Pair(colorPurple, "#E1BEE7")
        )

        for ((view, hex) in colorSquares) {
            view.setOnClickListener {
                selectedColour = hex
                refreshColorSquareBorders()
            }
        }

        // Image picker click listeners
        btnUploadImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnCapturePhoto.setOnClickListener {
            takePicture.launch(null)
        }

        btnRemoveImage.setOnClickListener {
            currentImageBase64 = null
            layoutImagePreview.visibility = View.GONE
        }

        // Date Picker
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

        // Check if editing an existing task
        if (editingTaskId != -1L) {
            tvScreenTitle.text = "Edit Task"
            btnDeleteTaskScreen.visibility = View.VISIBLE

            val existingTask = taskDao.getTaskById(editingTaskId)
            existingTask?.let { task ->
                etTitle.setText(task.title)
                etDeadline.setText(task.deadline)
                etDescription.setText(task.description)
                selectedColour = task.colour
                currentImageBase64 = task.img

                if (!task.img.isNullOrEmpty()) {
                    val decodedBytes = Base64.decode(task.img, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    ivTaskImage.setImageBitmap(bitmap)
                    layoutImagePreview.visibility = View.VISIBLE
                }
            }

            btnDeleteTaskScreen.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete") { _, _ ->
                        taskDao.deleteTask(editingTaskId)
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        } else {
            tvScreenTitle.text = "New Task"
            btnDeleteTaskScreen.visibility = View.GONE
        }

        refreshColorSquareBorders()

        btnBack.setOnClickListener {
            finish()
        }

        btnDone.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                if (editingTaskId != -1L) {
                    // Update existing task
                    val updatedTask = Task(
                        id = editingTaskId,
                        title = title,
                        deadline = deadline,
                        description = description,
                        colour = selectedColour,
                        img = currentImageBase64
                    )
                    taskDao.updateTask(updatedTask)
                    Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Insert new task
                    val newTask = Task(
                        title = title,
                        deadline = deadline,
                        description = description,
                        colour = selectedColour,
                        img = currentImageBase64
                    )
                    taskDao.insertTask(newTask)
                    Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun processAndSetImage(bitmap: Bitmap) {
        // Downscale image to max 500x500 to keep DB rows small and within SQLite window
        val maxDimension = 500
        val scale = minOf(
            maxDimension.toFloat() / bitmap.width,
            maxDimension.toFloat() / bitmap.height,
            1.0f
        )
        val scaledWidth = (bitmap.width * scale).toInt()
        val scaledHeight = (bitmap.height * scale).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()

        currentImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)

        ivTaskImage.setImageBitmap(scaledBitmap)
        layoutImagePreview.visibility = View.VISIBLE
    }

    private fun refreshColorSquareBorders() {
        for ((view, hex) in colorSquares) {
            val isSelected = (hex == selectedColour)
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