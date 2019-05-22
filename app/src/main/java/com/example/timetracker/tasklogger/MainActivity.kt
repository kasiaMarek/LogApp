package com.example.timetracker.tasklogger


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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




class MainActivity : AppCompatActivity() {

    private lateinit var tasklogger_adapter: TaskLoggerAdapter
    private lateinit var layout_manager: RecyclerView.LayoutManager
    private val task_list = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasklogger_main_activity)


        getTastks()
        initRecyclerView()

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


        JiraServiceKeeper.jira.getTasks()
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
                    body!!.issues.forEach { task_list.add(Task(it.key, it.fields.summary, it.fields.created ?: "","9:00", parseSeconds(it.fields.timespent ?: "0"),  "started")) }
                    initRecyclerView()
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }


    fun initRecyclerView(){
        initAdapter()

    }

    fun initAdapter(){
        layout_manager = LinearLayoutManager(this)
        tasklogger_adapter = TaskLoggerAdapter(task_list)

        tasklogger_recycler_view.layoutManager = layout_manager
        tasklogger_recycler_view.adapter = tasklogger_adapter
    }

}
