package com.example.timetracker

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.User
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        storage = Storage(this)
        setSupportActionBar(login_toolbar)
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layout.removeView(imageView)
        }
        if(savedInstanceState != null) {
            setErrorMessage(
                savedInstanceState.getString("errorMessage").orEmpty(),
                savedInstanceState.getInt("errorMessageColor")
            )
            input_login.editText?.setText(savedInstanceState.getString("login"))
            input_token.editText?.setText(savedInstanceState.getString("token"))
            input_project.editText?.setText(savedInstanceState.getString("project"))
        }
    }

    private fun setErrorMessage(message: String = getString(R.string.invalid_cred_error_msg), color : Int = Color.RED) {
        errorMessage.setTextColor(color)
        errorMessage.text = message
    }

    private fun checkCredentials(credentials : Credentials) {
        if(
            JiraServiceKeeper.initJira(
                credentials.login!!,
                credentials.token!!,
                credentials.projectName!!
            )
        ) {
            setErrorMessage(message = getString(R.string.waiting), color = Color.GRAY)
            val call = JiraServiceKeeper.jira.tryMyself()
            call.enqueue(object : Callback<User> {

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("Log", t.message)
                    setErrorMessage(message = getString(R.string.internet_problem))
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        JiraServiceKeeper.jira.name = body!!.displayName
                        storage.writeCredentials(credentials)
                        Log.d("Log", "you're successfully logged in")
                        setErrorMessage(message = "")
                        val i = Intent(baseContext, com.example.timetracker.tasklogger.MainActivity::class.java)
                        startActivity(i)
                    } else {
                        setErrorMessage()
                    }
                }
            })
        } else {
            setErrorMessage()
        }
    }

    fun onLoginClick(view: View) {
        val projectName = input_project.editText?.text.toString().trim()
        checkCredentials(
            Credentials(
                input_login.editText?.text.toString(),
                input_token.editText?.text.toString(),
                projectName
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("errorMessage", errorMessage.text.toString())
        outState.putInt("errorMessageColor", errorMessage.currentTextColor)
        outState.putString("login", input_login.editText?.text.toString())
        outState.putString("project", input_project.editText?.text.toString())
        outState.putString("token", input_token.editText?.text.toString())
    }

}
