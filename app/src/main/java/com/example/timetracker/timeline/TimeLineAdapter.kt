package com.example.timetracker.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_item.view.*
import androidx.appcompat.app.AlertDialog
import com.example.timetracker.model.TimelineAttributes
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
import com.example.timetracker.utils.ErrorUtils


class TimeLineAdapter(private val task_list: ArrayList<Worklog>, private var mAttributes: TimelineAttributes) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

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
        holder.task_date.text = DateObject(timeLineModel.started).stringFullDate
        holder.task_message.text = timeLineModel.issueSummary
        holder.task_time_spent.text = TimeObject(timeLineModel.timeSpentSeconds).string
        holder.task_id.text = timeLineModel.issueKey
    }


    override fun getItemCount() = task_list.size

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
                    val clickedDataItem = task_list[pos]

                    var context = v.context
                    val builder = AlertDialog.Builder(context)
                    var inflater = LayoutInflater.from(context)
                    val theView = inflater.inflate(R.layout.number_picker_dialog,null)
                    val hourPicker = theView.findViewById(R.id.hour_picker) as NumberPicker
                    val minutesPicker = theView.findViewById(R.id.minutes_picker) as NumberPicker
                    val taskTime = TimeObject(clickedDataItem.timeSpentSeconds).hoursAndMinutes


                    builder.setView(theView).setPositiveButton("Accept") { dialog, which ->
                        if(hourPicker.value != 0 || minutesPicker.value !=0){
                            val newTime = TimeObject(Pair(hourPicker.value, minutesPicker.value))
                            itemView.timeline_item_time_spent.text = newTime.string
                            task_list[pos].timeSpentSeconds = newTime.seconds.toString()
                            notifyDataSetChanged()
                            updateWorklog(context, clickedDataItem.issueId, clickedDataItem.id, newTime)
                        } else {
                            Toast.makeText(context, R.string.no_history_change_to_zero, Toast.LENGTH_SHORT).show()
                        }
                    }.setNegativeButton("Reject") { dialog, which -> }

                    hourPicker.apply {
                        minValue = 0
                        maxValue = 12
                        value = taskTime.first
                    }

                    minutesPicker.apply {
                        minValue = 0
                        maxValue = 59
                        value = taskTime.second
                    }

                    builder.show()
                }
            }

        }
    }

    fun updateWorklog(context : Context, issueId : String, worklogId : String, time : TimeObject) {
        val call = JiraServiceKeeper.jira.updateWorklog(issueId, worklogId, WorklogTime(time.seconds.toString()))

        call.enqueue(object : Callback<Worklog> {
            override fun onFailure(call: Call<Worklog>, t: Throwable) {
                ErrorUtils.unexpected(context)
            }

            override fun onResponse(call: Call<Worklog>, response: Response<Worklog>) {
                if(response.isSuccessful) {
                    Toast.makeText(context, R.string.success_update, Toast.LENGTH_SHORT).show()
                } else {
                    ErrorUtils.unexpected(context)
                }
            }

        })
    }
}
