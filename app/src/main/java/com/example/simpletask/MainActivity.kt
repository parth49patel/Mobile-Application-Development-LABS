package com.example.simpletask

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskDao = TaskDao(this)

        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)
        rvTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList())
        rvTasks.adapter = taskAdapter

        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            startActivity(Intent(this, NewTaskActivity::class.java))
        }

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Reload list immediately when coming back from NewTaskActivity
        loadTasks()
    }

    private fun loadTasks() {
        val currentQuery = searchView.query?.toString().orEmpty()
        if (currentQuery.isBlank()) {
            taskAdapter.updateTasks(taskDao.getAllTasks())
        } else {
            taskAdapter.updateTasks(taskDao.searchTasksByTitle(currentQuery))
        }
    }

    private fun performSearch(query: String?) {
        val result = if (query.isNullOrBlank()) {
            taskDao.getAllTasks()
        } else {
            taskDao.searchTasksByTitle(query.trim())
        }
        taskAdapter.updateTasks(result)
    }
}