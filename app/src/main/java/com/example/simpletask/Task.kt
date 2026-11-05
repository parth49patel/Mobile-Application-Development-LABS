package com.example.simpletask

data class Task(
    val id: Long = 0,
    val title: String,
    val deadline: String,
    val description: String,
    val colour: String,
    val createdUtc: String = ""
)
