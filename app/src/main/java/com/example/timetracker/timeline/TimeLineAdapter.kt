package com.example.timetracker.timeline

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_item.view.*
import androidx.appcompat.app.AlertDialog
import com.example.timetracker.model.Task
import com.example.timetracker.model.TimelineAttributes
import com.example.timetracker.utils.DateTimeUtils
import com.example.timetracker.utils.VectorDrawableUtils
import com.example.timetracker.R
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.Worklog
import com.example.timetracker.jiraservice.WorklogTime
import com.example.timetracker.model.DateObject
import com.example.timetracker.model.TimeObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimeLineAdapter(private val task_list: ArrayList<Task>, private var mAttributes: TimelineAttributes) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        view = layoutInflater.inflate(R.layout.timeline_item, parent, false)

        return TimeLineViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        val timeLineModel = task_list[position]

        holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.ic_marker_active,  mAttributes.markerColor)
        holder.task_date.text = DateObject(timeLineModel.date).stringFullDate
        holder.task_message.text = timeLineModel.title
        holder.task_time_spent.text = TimeObject(timeLineModel.time_spent).string
        holder.task_id.text = timeLineModel.task_id
    }


    override fun getItemCount() =task_list.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView){

        val timeline = itemView.timeline
        val task_date = itemView.timeline_item_date
        val task_message = itemView.timeline_item_title
        val task_time_spent = itemView.timeline_item_time_spent
        val task_id = itemView.timeline_item_id

        init {
            timeline.initLine(viewType)
            timeline.markerSize = mAttributes.markerSize
            timeline.setMarkerColor(mAttributes.markerColor)
            timeline.isMarkerInCenter = mAttributes.markerInCenter
            timeline.linePadding = mAttributes.linePadding

            timeline.lineWidth = mAttributes.lineWidth
            timeline.setStartLineColor(mAttributes.startLineColor, viewType)
            timeline.setEndLineColor(mAttributes.endLineColor, viewType)
            timeline.lineStyle = mAttributes.lineStyle
            timeline.lineStyleDashLength = mAttributes.lineDashWidth
            timeline.lineStyleDashGap = mAttributes.lineDashGap


            itemView.setOnClickListener { v ->                  // listener for clicking on a task, shows alert box
                // get position
                val pos = adapterPosition

                // check if item still exists
                if (pos != RecyclerView.NO_POSITION) {
                    //val clickedDataItem = task_list[pos]

                    var context = v.context
                    val builder = AlertDialog.Builder(context)

                    var inflater = LayoutInflater.from(context)

                    val theView = inflater.inflate(R.layout.number_picker_dialog,null)
                    val hour_picker = theView.findViewById(R.id.hour_picker) as NumberPicker
                    val minutes_picker = theView.findViewById(R.id.minutes_picker) as NumberPicker

                    hour_picker.value = Integer.parseInt(itemView.timeline_item_time_spent.text.toString())/3600
                    minutes_picker.value = (Integer.parseInt(itemView.timeline_item_time_spent.text.toString())%3600)/(60)

                    builder.setView(theView).setPositiveButton("Accept") { dialog, which ->
                        var new_value = ""

                        if(hour_picker.value == 0){
                            if(minutes_picker.value != 0 ){
                                new_value = minutes_picker.value.toString() + "min"
                            }
                        }else{
                            if(minutes_picker.value == 0 ){
                                new_value = hour_picker.value.toString() + "h"
                            }else{
                                new_value = hour_picker.value.toString() + "h " + minutes_picker.value.toString() + "min"
                            }
                        }

                        itemView.timeline_item_time_spent.text = new_value

                        //todo: send new values to jira?
                        Log.d("TEST ASD", itemView.timeline_item_time_spent.text.toString())

                    }.setNegativeButton("Reject") { dialog, which -> }

                    hour_picker.minValue = 0
                    hour_picker.maxValue = 12
                    minutes_picker.value = 1

                    minutes_picker.minValue = 0
                    minutes_picker.maxValue = 59
                    minutes_picker.value = 0

                    builder.show()

                }
            }

        }
    }

    fun updateWorklog(issue : String, id : String, time : String) {
        val call = JiraServiceKeeper.jira.updateWorklog(issue, id, WorklogTime(time))

        call.enqueue(object : Callback<Worklog> {

            override fun onFailure(call: Call<Worklog>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Worklog>, response: Response<Worklog>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("Log", "issue:" + body!!.started)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }
}
