package com.example.timetracker.tasklogger


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.model.Task
import com.example.timetracker.R
import kotlinx.android.synthetic.main.tasklogger_main_activity.*
import java.util.ArrayList




class MainActivity : AppCompatActivity() {

    private lateinit var tasklogger_adapter: TaskLoggerAdapter
    private lateinit var layout_manager: RecyclerView.LayoutManager
    private val task_list = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasklogger_main_activity)


        setDataListItems()
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


    }

    fun setDataListItems(){
        task_list.add(Task("ABC-123","Item successfully delivered", "","19:00","1h", "started"))
        task_list.add(Task("ZZZ -22","Courier is out to delivery your order", "2017-02-12 08:00", "9:07","1h", "started"))
        task_list.add(Task("DD","Item has reached courier facility at New Delhi", "2017-02-11 21:00","9:00","5h",  "started"))
        task_list.add(Task("OAO-2323","Item has been given to the courier", "2017-02-11 18:00","9:00","3h",  "done"))
        task_list.add(Task("666","Item is packed and will dispatch soon", "2017-02-11 09:30","19:00","0.5h",  "other"))
        task_list.add(Task("ADSD","Order is being readied for dispatch", "2017-02-11 08:00", "9:00","2h", "started"))
        task_list.add(Task("AMA","Order processing initiated", "2017-02-10 15:00","9:00","1h",  "done"))
        task_list.add(Task("KAJKO-2691","Order confirmed by seller", "2017-02-10 14:30","01:00","1h",  "started"))
        task_list.add(Task("DLK-3551","Order placed successfully", "2017-02-10 14:00", "21:00","0.333h", "done"))
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
