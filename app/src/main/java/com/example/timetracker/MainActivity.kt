package com.example.timetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.View
import com.example.timetracker.jiraservice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val jiraService = JiraService("maciekstosio@icloud.com", "UlFUaGOPDpa8sisMqf8B7C14")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onLoginClick(view: View) {
        val login = findViewById<TextInputLayout>(R.id.input_login).editText
        val password = findViewById<TextInputLayout>(R.id.input_password).editText

        Log.d("Log", login?.text.toString())
        Log.d("Log", password?.text.toString())

        val call = jiraService.tryMyself()
        call.enqueue(object : Callback<User> {

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    jiraService.name = body!!.displayName
                    getTastks()
                    getWorklogs()
                    addWorklog()
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    fun getTastks() {
        val call = jiraService.getTasks()!!
        call.enqueue(object : Callback<Tasks> {

            override fun onFailure(call: Call<Tasks>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Tasks>, response: Response<Tasks>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("Log", "issue:" + body!!.issues[0].fields.summary)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    fun getWorklogs() {
        val call = jiraService.getWorklog("10003")
        call.enqueue(object : Callback<Worklogs> {

            override fun onFailure(call: Call<Worklogs>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Worklogs>, response: Response<Worklogs>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("Log", "issue:" + body!!.worklogs[0].started)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    fun addWorklog() {
        val call = jiraService.addWorklog("10003", WorklogTime( "300"))
        call.enqueue(object : Callback<Worklog> {

            override fun onFailure(call: Call<Worklog>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Worklog>, response: Response<Worklog>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("Log", "issue:" + body!!.started)
                    updateWorklog(body!!.id)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    fun updateWorklog(id : String) {
        val call = jiraService.updateWorklog("10003", id, WorklogTime("600"))

        call.enqueue(object : Callback<Worklog> {

            override fun onFailure(call: Call<Worklog>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<Worklog>, response: Response<Worklog>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("Log", "issue:" + body!!.started)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }
}
