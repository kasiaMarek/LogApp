package com.example.timetracker.test_main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import com.example.timetracker.R


class MainActivity : AppCompatActivity() {

    //todo: activity for testing other activities (timeline, etc.)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity_main)
    }

    fun go_to_timeline(v: View){
        val i = Intent(baseContext, com.example.timetracker.timeline.MainActivity::class.java)
        startActivity(i)
    }

    fun go_to_tasks(v: View){
        val i = Intent(baseContext, com.example.timetracker.tasklogger.MainActivity::class.java)
        startActivity(i)
    }

    fun go_to_timer(v: View){

    }

}
