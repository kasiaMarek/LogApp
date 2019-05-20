package com.example.myapplication.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Task
import com.example.myapplication.model.TimelineAttributes
import com.example.myapplication.utils.DateTimeUtils
import com.example.myapplication.utils.VectorDrawableUtils
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_item.view.*
import androidx.appcompat.app.AlertDialog


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

        when {  //todo:  other states for task
            timeLineModel.status == "done" -> {
                holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.ic_marker_inactive, mAttributes.markerColor)
            }
            timeLineModel.status == "started"-> {
                holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.ic_marker_active,  mAttributes.markerColor)
            }
            else -> {
                holder.timeline.setMarker(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_marker), mAttributes.markerColor)
            }
        }

        if (timeLineModel.date.isNotEmpty()) {
            holder.task_date.visibility = View.VISIBLE
            holder.task_date.text = DateTimeUtils.parseDateTime(timeLineModel.date, "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy")
        } else
            holder.task_date.visibility = View.GONE

        holder.task_message.text = timeLineModel.title
        holder.task_time_spent.text = timeLineModel.time_spent
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

                    builder.setView(theView)                    // get the values from pickers and assign to item view
                        .setPositiveButton("Accept"
                        ) { dialog, which ->
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

                        }.setNegativeButton("Reject"
                        ) { dialog, which -> }

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

}
