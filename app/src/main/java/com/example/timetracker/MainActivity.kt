package com.example.timetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.User
import kotlinx.android.synthetic.main.activity_main.*
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
            checkCredentials(credentials, true)
        }
    }

    private fun checkCredentials(credentials : Credentials, saved: Boolean) {
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
                    if (!saved) errorMessage.text = getString(R.string.invalid_cred_error_msg)
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        JiraServiceKeeper.jira.name = body!!.displayName
                        if (!saved) {
                            storage.writeCredentials(credentials)
                        }
                        errorMessage.text = ""
                        // TODO:: go to task screen
                        getTastks()
                        getWorklogs()
                        addWorklog()
                        Log.d("Log", "you're successfully logged in")
                    } else {
                        if (!saved) errorMessage.text = getString(R.string.invalid_cred_error_msg)
                    }
                }
            })
        } else {
            if(!saved) errorMessage.text = getString(R.string.invalid_cred_error_msg)
        }

    }

    fun onLoginClick(view: View) {
        val projectName = input_project.editText?.text.toString().trim()
        val pattern = Regex("[a-zA-Z0-9]*")
        if(projectName != "" && pattern.matches(projectName)) {
            checkCredentials(
                Credentials(
                    input_login.editText?.text.toString(),
                    input_token.editText?.text.toString(),
                    projectName
                ),
                false
            )
        } else {
            errorMessage.text = getString(R.string.invalid_project_error_msg)
        }

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
