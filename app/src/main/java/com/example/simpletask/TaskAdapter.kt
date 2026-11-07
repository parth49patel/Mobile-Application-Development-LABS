package com.example.simpletask

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardTask)
        val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
        val tvDeadline: TextView = view.findViewById(R.id.tvItemDeadline)
        val tvDescription: TextView = view.findViewById(R.id.tvItemDescription)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTitle.text = task.title

        val deadlineText = task.deadline.trim()
        if (deadlineText.isNotEmpty()) {
            holder.tvDeadline.text = "Due: $deadlineText"
            holder.tvDeadline.visibility = View.VISIBLE
        } else {
            holder.tvDeadline.visibility = View.GONE
        }

        val descText = task.description.trim()
        if (descText.isNotEmpty()) {
            holder.tvDescription.text = descText
            holder.tvDescription.visibility = View.VISIBLE
        } else {
            holder.tvDescription.visibility = View.GONE
        }

        // Set Card Background Colour
        try {
            holder.card.setCardBackgroundColor(Color.parseColor(task.colour))
        } catch (e: Exception) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FFF59D"))
        }

        // Tap task card to open and edit
        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }

        // Delete button with confirmation AlertDialog
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete '${task.title}'?")
                .setPositiveButton("Delete") { _, _ ->
                    onTaskDelete(task)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        this.tasks = newTasks
        notifyDataSetChanged()
    }
}