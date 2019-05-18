package com.example.timetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.View
import com.example.timetracker.jiraservice.JiraService
import com.example.timetracker.jiraservice.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onLoginClick(view: View) {
        val login = findViewById<TextInputLayout>(R.id.input_login).editText
        val password = findViewById<TextInputLayout>(R.id.input_password).editText

        Log.d("Log", login?.text.toString())
        Log.d("Log", password?.text.toString())

        val jiraService = JiraService(login?.text.toString(), password?.text.toString())
        val call = jiraService.tryMyself()
        call.enqueue(object : Callback<User> {

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("Log", t.message)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful) {
                    val body = response.body()
                    Log.d("Log", body!!.displayName)
                } else {
                    Log.d("Log", "Wrong auth")
                }
            }

        })
    }
}
