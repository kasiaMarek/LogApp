package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent


class MainActivity : AppCompatActivity() {

    //todo: activity for testing other activities (timeline, etc.)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun go_to_timeline(v: View){
        val i = Intent(baseContext, com.example.myapplication.timeline.MainActivity::class.java)
        startActivity(i)
    }

    fun go_to_tasks(v: View){
        val i = Intent(baseContext, com.example.myapplication.tasklogger.MainActivity::class.java)
        startActivity(i)
    }

    fun go_to_timer(v: View){

    }

}
