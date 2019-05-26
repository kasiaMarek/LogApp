package com.example.timetracker.tasklogger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.model.Task
import com.example.timetracker.R
import com.example.timetracker.stoper.StopperActivity
import kotlinx.android.synthetic.main.tasklogger_item.view.*

class TaskLoggerAdapter(private val task_list: ArrayList<Task>, val context: Context) : RecyclerView.Adapter<TaskLoggerAdapter.TaskLoggerViewHolder>() {

    var task_list_copy = ArrayList<Task>()

    init{
        task_list_copy.addAll(task_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskLoggerViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        view = layoutInflater.inflate(R.layout.tasklogger_item, parent, false)

        return TaskLoggerViewHolder(view, viewType, context)
    }

    override fun onBindViewHolder(holder: TaskLoggerViewHolder, position: Int) {

        val timeLineModel = task_list[position]

        holder.task_title.text = timeLineModel.title
        holder.task_time_spent.text = timeLineModel.time_spent
        holder.task_id.text = timeLineModel.task_id

    }


    override fun getItemCount() =task_list.size

    inner class TaskLoggerViewHolder(itemView: View, viewType: Int, context: Context) : RecyclerView.ViewHolder(itemView) {


        val task_title = itemView.tasklogger_item_title
        val task_time_spent = itemView.tasklogger_item_time_spent
        val task_id = itemView.tasklogger_item_id

        init {

            itemView.setOnClickListener { v ->
                // listener for clicking on a task, shows alert box
                // get position
                val pos = adapterPosition

                // check if item still exists
                if (pos != RecyclerView.NO_POSITION) {
                    val clicked_task = task_list[pos]
                    //itemView.timeline_item_time_spent.text

                    val intent = Intent(context, StopperActivity::class.java).apply {
                        putExtra("task", clicked_task.title)
                        putExtra("taskId", clicked_task.task_num_id)
                    }
                    startActivity(context, intent, null)

                }
            }

        }
    }

    fun filter(text: String) {
        var text = text
        task_list.clear()
        if (text.isEmpty()) {
            task_list.addAll(task_list_copy)
        } else {
            text = text.toLowerCase()
            for (item in task_list_copy) {
                if (item.title.toLowerCase().contains(text) || item.task_id.toLowerCase().contains(text)) {
                    task_list.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}
