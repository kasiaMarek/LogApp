package com.example.timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.timetracker.jiraservice.JiraServiceKeeper
import com.example.timetracker.jiraservice.User
import com.example.timetracker.test_main.MainActivity
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
                    errorMessage.text = getString(R.string.invalid_cred_error_msg)
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        JiraServiceKeeper.jira.name = body!!.displayName
                        storage.writeCredentials(credentials)
                        errorMessage.text = ""

                        Log.d("Log", "you're successfully logged in")

                        val i = Intent(baseContext, MainActivity::class.java)
                        startActivity(i)

                    } else {
                        errorMessage.text = getString(R.string.invalid_cred_error_msg)
                    }
                }
            })
        } else {
            errorMessage.text = getString(R.string.invalid_cred_error_msg)
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
                )
            )
        } else {
            errorMessage.text = getString(R.string.invalid_project_error_msg)
        }

    }
}
