package com.example.timetracker.timeline
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.timetracker.model.TimelineAttributes
import com.example.timetracker.utils.Utils
import com.example.timetracker.R
import com.example.timetracker.jiraservice.*
import com.example.timetracker.model.DateObject
import com.example.timetracker.stats.Statistics
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var timeline_adapter: TimeLineAdapter
    private val task_list = ArrayList<Worklog>()
    private lateinit var layout_manager: LinearLayoutManager
    private lateinit var mAttributes: TimelineAttributes
    private var refreshing = false

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

        timeline_swipe_refresh.post {
            timeline_swipe_refresh.isRefreshing = true
            getTastks()
        }
    }

    override fun onRefresh() {
        if (refreshing == false ) {
            refreshing = true
            getTastks()
        }
    }

    fun getTastks() {
        val call = JiraServiceKeeper.jira.getTasks()!!
        call.enqueue(object : Callback<Tasks> {

            override fun onFailure(call: Call<Tasks>, t: Throwable) {
                Toast.makeText(this@MainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Tasks>, response: Response<Tasks>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    task_list.clear()
                    body!!.issues.forEach { getWorklogs(it) }
                } else {
                    Toast.makeText(this@MainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
                }
                refreshing  = false
            }

        })
        timeline_swipe_refresh.isRefreshing = false
    }

    fun getWorklogs(task : Issue) {
        val call = JiraServiceKeeper.jira.getWorklog(task.key)
        call.enqueue(object : Callback<Worklogs> {

            override fun onFailure(call: Call<Worklogs>, t: Throwable) {
                Toast.makeText(this@MainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Worklogs>, response: Response<Worklogs>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    body!!.worklogs.forEach {
                        it.issueKey = task.key
                        it.issueSummary = task.fields.summary
                    }
                    task_list.addAll(body!!.worklogs)
                    initRecyclerView()
                } else {
                    Toast.makeText(this@MainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
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
            this.finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        task_list.sortByDescending{ DateObject(it.started).stringDate }
        initAdapter()
    }

    private fun initAdapter() {
        layout_manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        timeline_adapter = TimeLineAdapter(task_list, mAttributes)

        timeline_recycler_view.layoutManager = layout_manager
        timeline_recycler_view.adapter = timeline_adapter
    }
}