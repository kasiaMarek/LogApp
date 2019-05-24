package com.example.timetracker.stats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timetracker.R
import android.R.attr.entries
import androidx.core.view.marginBottom
import com.github.mikephil.charting.components.Description
import kotlinx.android.synthetic.main.activity_statistics.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*


class Statistics : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val dataObjects = arrayOf(Pair(2,3), Pair(3,4), Pair(4,7))
        val entries = ArrayList<BarEntry>();
        for (data in dataObjects) {
            // turn your data into Entry objects
            entries.add(BarEntry(data.first.toFloat(), data.second.toFloat()))
        }
        val dataSet = BarDataSet(entries, "Label")
        val barData = BarData(dataSet)
        barData.setValueTextSize(10f) //text size above bars
        chart.data = barData
        chart.setDrawGridBackground(false)
        //no grid
        chart.axisRight.setDrawLabels(false)
        chart.axisRight.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        //x-axis to bottom
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.legend.isEnabled = false
        chart.description = Description().apply { this.text = "" }

        chart.xAxis.textSize = 20f
        chart.axisLeft.textSize = 20f
        chart.invalidate()
    }
}
