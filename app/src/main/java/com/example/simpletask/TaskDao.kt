package com.example.simpletask

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class TaskDao(context: Context) {
    private val dbHelper = TaskDatabaseHelper(context)

    fun insertTask(task: Task): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COLUMN_TITLE, task.title)
            put(TaskDatabaseHelper.COLUMN_DEADLINE, task.deadline)
            put(TaskDatabaseHelper.COLUMN_DESCRIPTION, task.description)
            put(TaskDatabaseHelper.COLUMN_COLOUR, task.colour)
            put(TaskDatabaseHelper.COLUMN_IMG, task.img)
        }
        return db.insert(TaskDatabaseHelper.TABLE_TASKS, null, values)
    }

    fun updateTask(task: Task): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COLUMN_TITLE, task.title)
            put(TaskDatabaseHelper.COLUMN_DEADLINE, task.deadline)
            put(TaskDatabaseHelper.COLUMN_DESCRIPTION, task.description)
            put(TaskDatabaseHelper.COLUMN_COLOUR, task.colour)
            put(TaskDatabaseHelper.COLUMN_IMG, task.img)
        }
        return db.update(
            TaskDatabaseHelper.TABLE_TASKS,
            values,
            "${TaskDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(task.id.toString())
        )
    }

    fun deleteTask(taskId: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            TaskDatabaseHelper.TABLE_TASKS,
            "${TaskDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(taskId.toString())
        )
    }

    fun getTaskById(taskId: Long): Task? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TaskDatabaseHelper.TABLE_TASKS,
            null,
            "${TaskDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(taskId.toString()),
            null,
            null,
            null
        )
        val list = mutableListOf<Task>()
        parseCursor(cursor, list)
        return list.firstOrNull()
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TaskDatabaseHelper.TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "${TaskDatabaseHelper.COLUMN_ID} DESC"
        )
        parseCursor(cursor, taskList)
        return taskList
    }

    fun searchTasksByTitle(query: String): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TaskDatabaseHelper.TABLE_TASKS,
            null,
            "${TaskDatabaseHelper.COLUMN_TITLE} LIKE ?",
            arrayOf("%$query%"),
            null,
            null,
            "${TaskDatabaseHelper.COLUMN_ID} DESC"
        )
        parseCursor(cursor, taskList)
        return taskList
    }

    private fun parseCursor(cursor: Cursor, taskList: MutableList<Task>) {
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val imgIndex = it.getColumnIndex(TaskDatabaseHelper.COLUMN_IMG)
                    val img = if (imgIndex != -1 && !it.isNull(imgIndex)) it.getString(imgIndex) else null

                    val task = Task(
                        id = it.getLong(it.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TITLE)),
                        deadline = it.getString(it.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_DEADLINE)) ?: "",
                        description = it.getString(it.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_DESCRIPTION)) ?: "",
                        colour = it.getString(it.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_COLOUR)),
                        createdUtc = it.getString(it.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_CREATED_UTC)),
                        img = img
                    )
                    taskList.add(task)
                } while (it.moveToNext())
            }
        }
    }
}