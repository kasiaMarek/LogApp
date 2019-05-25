package com.example.timetracker.stats

import android.content.Context
import com.example.timetracker.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.timetracker.model.DateObject
import com.example.timetracker.model.TimeObject
import kotlinx.android.synthetic.main.activity_statistics_item.view.*


class StatisticsAdapter(context: Context, users: List<Pair<DateObject, TimeObject>>) : ArrayAdapter<Pair<DateObject, TimeObject>>(context, 0, users) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val item = getItem(position)

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_statistics_item, parent, false)
        }

        convertView!!.time.text = item.second.hours.toString()
        convertView!!.name.text = item.first.stringShortDate

        return convertView
    }
}