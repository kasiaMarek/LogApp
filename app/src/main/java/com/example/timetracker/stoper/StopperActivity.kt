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

class StopperActivity : AppCompatActivity() {
    lateinit var task: String
    var stop : ImageButton? = null
    var start : ImageButton? = null
    var save : ImageButton? = null
    var on : Boolean = false
    var startTime:LocalDateTime? = null
    var totalTime:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopper)
        task = intent.getStringExtra("task")
        taskName.text = task
        if(savedInstanceState != null) {
            on = savedInstanceState.getBoolean("on")
            totalTime = savedInstanceState.getInt("totalTime")
            changeTime(totalTime)
        }
        if(on) {
            countTime()
            putStopIcon()
        } else {
            putStartIcon()
        }
    }

    private fun changeTime(diff : Int) {
        val time = Time(diff)
        runOnUiThread {
            if (sec.text.toString().toInt() != time.sec) {
                sec.text = time.secS
            }
            if (min.text.toString().toInt() != time.min) {
                min.text = time.minS
            }
            if (hour.text.toString().toInt() != time.hour) {
                hour.text = time.hourS
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

    private fun createImageButton(drawable : Int, onClick: () -> Unit) : ImageButton {
        val param = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        return ImageButton( this ).apply {
            setImageResource(drawable)
            setOnClickListener{ onClick() }
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = param
        }
    }

    private fun putStopIcon() {
        val stop = createImageButton(R.drawable.pause) {stop()}
        iconRow.addView(stop)
        this.stop = stop
    }

    private fun putStartIcon() {
        val start = createImageButton(R.drawable.play) {start()}
        iconRow.addView(start)
        this.start = start
        val save = createImageButton(R.drawable.check) {save()}
        iconRow.addView(save)
        this.save = save
    }

    private fun getTimeFromClock() : Int {
        return sec.text.toString().toInt() + min.text.toString().toInt()*60 + hour.text.toString().toInt()*60*60
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        if(on) totalTime += getTimeFromClock()
        savedInstanceState.putBoolean("on", on)
        savedInstanceState.putInt("totalTime", totalTime)
    }

    private class Time(diff : Int) {
        val sec = diff%60
        val min =  (diff/60)%60
        val hour = ((diff/60)/60)
        val secS = valToString(sec)
        val minS = valToString(min)
        val hourS = valToString(hour)

        fun valToString(v:Int) : String = if(v < 10) "0$v" else v.toString()
    }
}
