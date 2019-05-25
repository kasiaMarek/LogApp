package com.example.timetracker.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.model.Task
import com.example.timetracker.model.TimelineAttributes
import com.example.timetracker.utils.Utils
import com.example.timetracker.R
import com.example.timetracker.jiraservice.Issue
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.Tasks
import com.example.timetracker.jiraservice.Worklogs
import com.example.timetracker.utils.DateTimeUtils
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MainActivity : BaseActivity() {

    // todo: main timeline activity

    private lateinit var timeline_adapter: TimeLineAdapter
    private val task_list = ArrayList<Task>()
    private lateinit var layout_manager: LinearLayoutManager
    private lateinit var mAttributes: TimelineAttributes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentViewWithoutInject(R.layout.timeline_main_activity)

        //default values
        mAttributes = TimelineAttributes(
            markerSize = Utils.dpToPx(20f, this),
            markerColor = ContextCompat.getColor(this, R.color.material_grey_500),
            markerInCenter = true,
            linePadding = Utils.dpToPx(2f, this),
            startLineColor = ContextCompat.getColor(this, R.color.colorAccent),
            endLineColor = ContextCompat.getColor(this, R.color.colorAccent),
            lineStyle = TimelineView.LineStyle.NORMAL,
            lineWidth = Utils.dpToPx(2f, this),
            lineDashWidth = Utils.dpToPx(4f, this),
            lineDashGap = Utils.dpToPx(2f, this)
        )

        getTastks()

        timeline_refresh_button.setOnClickListener {refresh()}

    }

    //todo: delete, just to check if adapter works
    fun refresh(){
        task_list.clear()
        getTastks()
        timeline_adapter.notifyDataSetChanged()
    }


    fun getTastks() {
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
                    body!!.worklogs.forEach {task_list.add(Task(task.key, it.id, task.fields.summary, it.started,"9:00", it.timeSpentSeconds,"started")) }
                    initRecyclerView()
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    private fun initRecyclerView() {
        initAdapter()
        timeline_recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            @SuppressLint("LongLogTag")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                timeline_dropshadow.visibility = if(recyclerView.getChildAt(0).top < 0) View.VISIBLE else View.GONE
            }
        })
    }

    private fun initAdapter() {

        layout_manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        timeline_adapter = TimeLineAdapter(task_list, mAttributes)

        timeline_recycler_view.layoutManager = layout_manager
        timeline_recycler_view.adapter = timeline_adapter
    }

}