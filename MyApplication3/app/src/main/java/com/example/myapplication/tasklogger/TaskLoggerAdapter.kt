package com.example.myapplication.tasklogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Task
import kotlinx.android.synthetic.main.tasklogger_item.view.*




class TaskLoggerAdapter(private val task_list: ArrayList<Task>) : RecyclerView.Adapter<TaskLoggerAdapter.TaskLoggerViewHolder>() {

    var task_list_copy = ArrayList<Task>()

    init{
        task_list_copy.addAll(task_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskLoggerAdapter.TaskLoggerViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        view = layoutInflater.inflate(R.layout.tasklogger_item, parent, false)

        return TaskLoggerViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TaskLoggerAdapter.TaskLoggerViewHolder, position: Int) {

        val timeLineModel = task_list[position]

        holder.task_title.text = timeLineModel.title
        holder.task_time_spent.text = timeLineModel.time_spent
        holder.task_id.text = timeLineModel.task_id

    }


    override fun getItemCount() =task_list.size

    inner class TaskLoggerViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {


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

                    Toast.makeText(
                        itemView.context,
                        "todo: open stopwatch with id " + clicked_task.task_id,
                        Toast.LENGTH_SHORT
                    ).show()
                    //todo: new intent to stopwatch

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