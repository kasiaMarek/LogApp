package com.example.timetracker.timeline
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.timetracker.model.Task
import com.example.timetracker.model.TimelineAttributes
import com.example.timetracker.utils.Utils
import com.example.timetracker.R
import com.example.timetracker.jiraservice.Issue
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.Tasks
import com.example.timetracker.jiraservice.Worklogs
import com.example.timetracker.stats.Statistics
import com.example.timetracker.tasklogger.MainActivity
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    // todo: main timeline activity

    private lateinit var timeline_adapter: TimeLineAdapter
    private val task_list = ArrayList<Task>()
    private lateinit var layout_manager: LinearLayoutManager
    private lateinit var mAttributes: TimelineAttributes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timeline_main_activity)
        setSupportActionBar(timeline_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        setRefreshSwipe()

    }

    fun setRefreshSwipe(){
        timeline_swipe_refresh.setOnRefreshListener(this)

        timeline_swipe_refresh.post(Runnable {
            timeline_swipe_refresh.setRefreshing(true)

            Log.d("Log", "Initial fetch of data")
            getTastks()
        })

    }

    override fun onRefresh() {
        getTastks()
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
        timeline_swipe_refresh.setRefreshing(false);
        Log.d("Log", "Refreshed!")
    }

    fun getWorklogs(task : Issue) {
        val call = JiraServiceKeeper.jira.getWorklog(task.key)
        call.enqueue(object : Callback<Worklogs> {

            override fun onFailure(call: Call<Worklogs>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Worklogs>, response: Response<Worklogs>) {
                if(response.isSuccessful) {
                    // also delete previous entries from task_list
                    task_list.clear()
                    val body = response.body()
                    body!!.worklogs.forEach {task_list.add(Task(task.key, it.id, task.fields.summary, it.started,"9:00", it.timeSpentSeconds,"started")) }
                    initRecyclerView()
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_with_next, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val settingsItem = menu.findItem(R.id.next_activity)
        settingsItem.setIcon(R.drawable.goto_stats)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.getItemId()

        if (id == R.id.next_activity) {
            val i = Intent(this, Statistics::class.java)
            startActivity(i)
        }else if ( id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        initAdapter()
    }

    private fun initAdapter() {

        layout_manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        timeline_adapter = TimeLineAdapter(task_list, mAttributes)

        timeline_recycler_view.layoutManager = layout_manager
        timeline_recycler_view.adapter = timeline_adapter
    }

}