package com.example.simpletask

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "simple_task.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DEADLINE = "deadline"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_COLOUR = "colour"
        const val COLUMN_CREATED_UTC = "created_utc"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DEADLINE TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_COLOUR TEXT NOT NULL,
                $COLUMN_CREATED_UTC TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }
}