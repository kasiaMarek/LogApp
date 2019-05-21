package com.example.timetracker.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.model.Task
import com.example.timetracker.model.TimelineAttributes
import com.example.timetracker.utils.Utils
import com.example.timetracker.R
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.timeline_main_activity.*
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

        setDataListItems()
        initRecyclerView()

        timeline_refresh_button.setOnClickListener {refresh()}

    }

    //todo: delete, just to check if adapter works
    fun refresh(){
        task_list.add(Task("DLK-3551","Order placed successfully", "2017-02-10 14:00", "21:00","0.333h", "done"))
        timeline_adapter.notifyDataSetChanged()
    }

    private fun setDataListItems() {

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