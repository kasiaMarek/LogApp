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

const val errorMessageText = "Provided credentials are invalid. Please try again."

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
                    if (!saved) errorMessage.text = errorMessageText
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
                        Log.d("Log", "you're successfully logged in")
                    } else {
                        if (!saved) errorMessage.text = errorMessageText
                    }
                }
            })
        } else {
            if(!saved) errorMessage.text = errorMessageText
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
            errorMessage.text = "$projectName is not a valid project name"
        }

    }

}
