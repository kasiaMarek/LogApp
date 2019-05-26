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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val i = Intent(baseContext, LoginActivity::class.java)
            startActivity(i)
        }
    }
}
