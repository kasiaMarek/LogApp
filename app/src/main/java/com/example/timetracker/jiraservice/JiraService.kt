package com.example.timetracker.jiraservice

import android.util.Base64
import android.util.Log

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface JiraApi {
    @Headers("Content-Type: application/json")
    @GET("myself")
    fun myself(@Header("Authorization") token : String) : Call<User>

    @Headers("Content-Type: application/json")
    @GET("search")
    fun tasks(@Header("Authorization") token : String, @Query("assignee") assignee : String) : Call<Tasks>

}

class JiraService(login : String, token : String) {
    val auth = "Basic " + Base64.encodeToString(("$login:$token").toByteArray(), Base64.NO_WRAP)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://logapp.atlassian.net/rest/api/2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val jira = retrofit.create(JiraApi::class.java)
    var name : String? = null

    fun tryMyself() : Call<User> {
        return jira.myself(auth)
    }

    fun getTasks() : Call<Tasks>? {
        return if (name != null) jira.tasks(auth, name!!) else null
    }
}

object JiraServiceKeeper {
    var jira : JiraService? = null

    fun initJira(login : String, token : String) {
        jira = JiraService(login, token)
    }
}

data class User(val displayName:String)

data class Tasks(val total : Int, val issues : List<Issue>)
data class Issue(val id : String, val key : String, val fields : Fields)
data class Fields(val summary : String, val description : String?)