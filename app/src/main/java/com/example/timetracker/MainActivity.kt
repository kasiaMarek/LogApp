package com.example.timetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.timetracker.jiraservice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var storage : Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storage = Storage(this)
        val credentials = storage.getCredentials()
        if(credentials.checkIfInit()) {
            checkCredentials(credentials)
        } else {
            val i = Intent(baseContext, LoginActivity::class.java)
            startActivity(i)
        }
    }
    private fun checkCredentials(credentials : Credentials) {
        if(
            JiraServiceKeeper.initJira(
                credentials.login!!,
                credentials.token!!,
                credentials.projectName!!
            )
        ) {
            val call = JiraServiceKeeper.jira.tryMyself()
            call.enqueue(object : Callback<User> {

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("Log", t.message)
                    val i = Intent(baseContext, LoginActivity::class.java)
                    startActivity(i)
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        JiraServiceKeeper.jira.name = body!!.displayName
                        val i = Intent(baseContext, com.example.timetracker.tasklogger.MainActivity::class.java)
                        startActivityForResult(i, 123)
                    } else {
                        val i = Intent(baseContext, LoginActivity::class.java)
                        startActivity(i)
                    }
                }
            })
        } else {
            val i = Intent(baseContext, LoginActivity::class.java)
            startActivity(i)
        }

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
                    Log.d("Log", "issue:" + body!!.issues[0].fields.summary)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }

    fun getWorklogs() {
        val call = JiraServiceKeeper.jira.getWorklog("10003")
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
        val call = JiraServiceKeeper.jira.addWorklog("10003", WorklogTime( "300"))
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
        val call = JiraServiceKeeper.jira.updateWorklog("10003", id, WorklogTime("600"))

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val i = Intent(baseContext, LoginActivity::class.java)
            startActivity(i)
        }
    }
}
