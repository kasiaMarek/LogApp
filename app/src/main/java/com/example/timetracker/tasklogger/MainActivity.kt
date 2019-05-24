package com.example.timetracker.tasklogger


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.timetracker.model.Task
import com.example.timetracker.R
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.Tasks
import com.example.timetracker.utils.DateTimeUtils.parseSeconds
import kotlinx.android.synthetic.main.tasklogger_main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import android.view.Menu
import android.view.MenuItem
import com.example.timetracker.timeline.MainActivity


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var tasklogger_adapter: TaskLoggerAdapter
    private lateinit var layout_manager: RecyclerView.LayoutManager
    private val task_list = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasklogger_main_activity)

        setSupportActionBar(tasklogger_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tasklogger_searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                tasklogger_adapter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                tasklogger_adapter.filter(newText)
                return true
            }
        })


     setRefreshSwipe()
    }

    fun setRefreshSwipe(){
        tasklogger_swipe_refresh.setOnRefreshListener(this)

        tasklogger_swipe_refresh.post(Runnable {
            tasklogger_swipe_refresh.setRefreshing(true)

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
                    // also delete previous entries from task_list
                    task_list.clear()
                    val body = response.body()
                    body!!.issues.forEach { task_list.add(Task(it.key, it.id, it.fields.summary, it.fields.created ?: "","9:00", parseSeconds(it.fields.timespent ?: "0"),  "started")) }
                    initRecyclerView()
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })

        tasklogger_swipe_refresh.setRefreshing(false);
        Log.d("Log", "Refreshed!")
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
            val i = Intent(baseContext, MainActivity::class.java)
            startActivity(i)
            return true
        }else if ( id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    fun initRecyclerView(){
        initAdapter()

    }

    fun initAdapter(){
        layout_manager = LinearLayoutManager(this)
        tasklogger_adapter = TaskLoggerAdapter(task_list, this)

        tasklogger_recycler_view.layoutManager = layout_manager
        tasklogger_recycler_view.adapter = tasklogger_adapter
    }

}
