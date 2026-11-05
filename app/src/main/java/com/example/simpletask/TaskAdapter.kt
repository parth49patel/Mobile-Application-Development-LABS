package com.example.simpletask

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private var tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardTask)
        val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
        val tvDeadline: TextView = view.findViewById(R.id.tvItemDeadline)
        val tvDescription: TextView = view.findViewById(R.id.tvItemDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Set Title
        holder.tvTitle.text = task.title

        // Handle Deadline visibility and text
        if (task.deadline.isNotBlank()) {
            holder.tvDeadline.text = "Due: ${task.deadline}"
            holder.tvDeadline.visibility = View.VISIBLE
        } else {
            holder.tvDeadline.visibility = View.GONE
        }

        // Handle Description visibility and text
        if (task.description.isNotBlank()) {
            holder.tvDescription.text = task.description
            holder.tvDescription.visibility = View.VISIBLE
        } else {
            // GONE removes the element and its margins from height measurement
            holder.tvDescription.visibility = View.GONE
        }

        // Set Card Background Colour
        try {
            holder.card.setCardBackgroundColor(Color.parseColor(task.colour))
        } catch (e: Exception) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FFF59D"))
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        this.tasks = newTasks
        notifyDataSetChanged()
    }
}