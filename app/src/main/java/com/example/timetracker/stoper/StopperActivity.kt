package com.example.timetracker.stoper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.timetracker.R
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_stopper.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import java.util.*

class StopperActivity : AppCompatActivity() {
    private val param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
    lateinit var task:String
    var stop : ImageButton? = null
    var start : ImageButton? = null
    var save : ImageButton? = null
    var on : Boolean = false
    var startTime:Float? = null
    var totalTime:Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopper)
//        task = intent.getStringExtra("taskName")
//        taskName.text = task
        if(savedInstanceState != null) on = savedInstanceState.getBoolean("on")
        if(on) {
            putStopIcon()
        } else {
            putStartIcon()
        }
    }

    fun stop() {
        layout.removeView(stop)
        on = false
        putStartIcon()
    }

    fun start() {
        layout.removeView(save)
        layout.removeView(start)
        on = true
        putStopIcon()
    }

    fun save() {

    }

//    private fun countTime() {
//        startTime = Calendar.getInstance().getTime();
//    }

    private fun putStopIcon() {
        val stop = ImageButton( this )
        stop.setImageResource(R.drawable.pause)
        stop.setOnClickListener{ stop() }
        stop.setBackgroundColor(Color.TRANSPARENT)
        iconRow.addView(stop)
        stop.layoutParams = param
//        layout.addView(stop)
//        stop.id = View.generateViewId()
//        val c = ConstraintSet()
//        c.clone(layout)
//        c.connect(stop.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 8)
//        c.connect(stop.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 8)
//        c.connect(stop.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
//        c.connect(stop.id, ConstraintSet.TOP, clock.id, ConstraintSet.BOTTOM, 0)
//        c.applyTo(layout)
        this.stop = stop
    }

    private fun putStartIcon() {
//        val row = LinearLayout(this)
//        row.id = View.generateViewId()
//        row.layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        row.orientation = LinearLayout.HORIZONTAL
        val start = ImageButton( this )
        val save = ImageButton(this)
        start.setImageResource(R.drawable.play)
        save.setImageResource(R.drawable.check)
        save.setOnClickListener { save() }
        start.setOnClickListener { start() }
        start.setBackgroundColor(Color.TRANSPARENT)
        save.setBackgroundColor(Color.TRANSPARENT)
        save.layoutParams = param
        start.layoutParams = param
        iconRow.addView(save)
        iconRow.addView(start)
//        layout.addView(row)
//        val c = ConstraintSet()
//        c.clone(layout)
//        c.connect(row.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
//        c.connect(row.id, ConstraintSet.TOP, clock.id, ConstraintSet.BOTTOM, 0)
//        c.applyTo(layout)
//        this.startRow = row
    }


    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("on", on)
    }
}
