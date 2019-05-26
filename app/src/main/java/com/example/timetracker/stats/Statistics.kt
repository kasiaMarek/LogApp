package com.example.timetracker.stats

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timetracker.R
import kotlinx.android.synthetic.main.activity_statistics.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.YAxis
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.example.timetracker.Storage
import com.example.timetracker.jiraservice.Issue
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.Tasks
import com.example.timetracker.jiraservice.Worklogs
import com.example.timetracker.model.DateObject
import com.example.timetracker.model.TimeObject
import com.example.timetracker.timeline.MainActivity
import com.example.timetracker.timeline.whenNotNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Statistics : AppCompatActivity() {
    val data = ArrayList<Pair<DateObject, TimeObject>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setSupportActionBar(statisticsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getData()
    }

    fun getData() {
        val call = JiraServiceKeeper.jira.getTasks()!!
        call.enqueue(object : Callback<Tasks> {

            override fun onFailure(call: Call<Tasks>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Tasks>, response: Response<Tasks>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    body!!.issues.forEach { getWorklogs(it) }
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    fun getWorklogs(task : Issue) {
        val call = JiraServiceKeeper.jira.getWorklog(task.key)
        call.enqueue(object : Callback<Worklogs> {

            override fun onFailure(call: Call<Worklogs>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Worklogs>, response: Response<Worklogs>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    body!!.worklogs.forEach { data.add(Pair(DateObject(it.started), TimeObject(it.timeSpentSeconds))) }
                    refreashChart()
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }
        })
    }

    fun refreashChart() {
        val groupedData = data.groupBy{ it.first.stringDate }
        val parsedData = groupedData.map{ Pair(it.value[0].first, TimeObject(it.value.sumBy{it.second.seconds}))}
        val sortedData = parsedData.sortedBy { it.first.stringDate }
        drawChart(sortedData)
        showList(sortedData)
    }

    fun drawChart(data : List<Pair<DateObject, TimeObject>>) {
        val labels = data.map{ it.first.stringShortDate}
        val values = data.mapIndexed{index, value -> BarEntry(index.toFloat(), value.second.hours)}

        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)

        val xaxis = chart.xAxis
        xaxis.setDrawGridLines(false)
        xaxis.position = XAxis.XAxisPosition.BOTTOM
        xaxis.granularity = 1f
        xaxis.setDrawLabels(true)
        xaxis.setDrawAxisLine(true)
        xaxis.valueFormatter = IndexAxisValueFormatter(labels)

        val yAxisLeft = chart.axisLeft
        yAxisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.setDrawAxisLine(false)
        yAxisLeft.isEnabled = false

        chart.axisRight.isEnabled = false

        val legend = chart.legend
        legend.isEnabled = false

        val barDataSet = BarDataSet(values, " ")
        barDataSet.color = ContextCompat.getColor(this, R.color.colorPrimary)
        barDataSet.setDrawValues(true)

        val data = BarData(barDataSet)
        chart.data = data
        chart.invalidate()
    }

    fun showList(data : List<Pair<DateObject, TimeObject>>) {
        list.adapter = StatisticsAdapter(this, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if ( id == android.R.id.home) {
            this.finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
