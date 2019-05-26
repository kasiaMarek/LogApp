package com.example.timetracker.stoper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.timetracker.R
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_stopper.*
import android.widget.LinearLayout
import android.widget.Toast
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.Worklog
import com.example.timetracker.jiraservice.WorklogTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalDateTime
import android.R.attr.data

class StopperActivity : AppCompatActivity() {
    private val param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
    lateinit var task: String
    var stop : ImageButton? = null
    var start : ImageButton? = null
    var save : ImageButton? = null
    var on : Boolean = false
    var startTime:LocalDateTime? = null
    var totalTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopper)
        task = intent.getStringExtra("task")
        taskName.text = task
        if(savedInstanceState != null) {
            on = savedInstanceState.getBoolean("on")
            totalTime = savedInstanceState.getLong("totalTime")
            changeTime(totalTime)
        }
        if(on) {
            countTime()
            putStopIcon()
        } else {
            putStartIcon()
        }
    }

    private fun changeTime(diff : Long) {
        val sect = (diff%60)
        val mint = ((diff/60)%60)
        val hourt = ((diff/60)/60)
        val sectS = if(sect < 10)  "0$sect" else sect.toString()
        val mintS = if(mint < 10)  "0$mint" else mint.toString()
        val hourtS = if(hourt < 10)  "0$hourt" else hourt.toString()
        runOnUiThread {
            if (sec.text.toString().toLong() != sect) {
                sec.text = sectS
            }
            if (min.text.toString().toLong() != mint) {
                min.text = mintS
            }
            if (hour.text.toString().toLong() != hourt) {
                hour.text = hourtS
            }
        }

    }

    private fun countTime() {
        startTime = LocalDateTime.now()
        Thread{
            while (on) {
                Thread.sleep(300)
                changeTime(Duration.between(startTime, LocalDateTime.now()).seconds.toInt() + totalTime)
            }
            totalTime += Duration.between(startTime, LocalDateTime.now()).seconds.toInt()
        }.start()
    }

    private fun stop() {
        iconRow.removeView(stop)
        on = false
        putStartIcon()
    }

    private fun start() {
        iconRow.removeView(save)
        iconRow.removeView(start)
        on = true
        putStopIcon()
        countTime()
    }

    private fun save() {
        if(totalTime >= 60) {
            JiraServiceKeeper.jira.addWorklog(
                intent.getStringExtra("taskId"),
                WorklogTime(totalTime.toString())
            ).enqueue(object : Callback<Worklog> {
                override fun onResponse(call: Call<Worklog>, response: Response<Worklog>) {}
                override fun onFailure(call: Call<Worklog>, t: Throwable) {}
            })
        } else {
            Toast.makeText(
                this,
                getString(R.string.no_log_under_minute),
                Toast.LENGTH_LONG
            ).show()
        }
        finish()

    }

    private fun putStopIcon() {
        val stop = ImageButton( this )
        stop.setImageResource(R.drawable.pause)
        stop.setOnClickListener{ stop() }
        stop.setBackgroundColor(Color.TRANSPARENT)
        iconRow.addView(stop)
        stop.layoutParams = param
        this.stop = stop
    }

    private fun putStartIcon() {
        val start = ImageButton( this )
        val save = ImageButton(this)
        start.setImageResource(R.drawable.play)
        save.setImageResource(R.drawable.check)
        start.setOnClickListener { start() }
        save.setOnClickListener { save() }
        start.setBackgroundColor(Color.TRANSPARENT)
        save.setBackgroundColor(Color.TRANSPARENT)
        save.layoutParams = param
        start.layoutParams = param
        iconRow.addView(start)
        iconRow.addView(save)
        this.start = start
        this.save = save
    }

    private fun getTimeFromClock() : Long {
        return sec.text.toString().toLong() + min.text.toString().toLong()*60 + hour.text.toString().toLong()*60*60
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        if(on) totalTime += getTimeFromClock()
        savedInstanceState.putBoolean("on", on)
        savedInstanceState.putLong("totalTime", totalTime)
    }
}
