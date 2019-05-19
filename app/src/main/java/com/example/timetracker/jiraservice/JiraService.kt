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

    @Headers("Content-Type: application/json")
    @GET("issue/{issue}/worklog")
    fun getWorklog(@Header("Authorization") token : String, @Path("issue") issue : String) : Call<Worklogs>

    @Headers("Content-Type: application/json")
    @POST("issue/{issue}/worklog")
    fun addWorklog(@Header("Authorization") token : String, @Path("issue") issue : String, @Body worklogData : WorklogTime) : Call<Worklog>

    @Headers("Content-Type: application/json")
    @POST("issue/{issue}/worklog/{worklog}")
    fun updateWorklog(@Header("Authorization") token : String, @Path("issue") issue : String, @Path("worklog") worklog : String, @Body worklogData : WorklogTime) : Call<Worklog>
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

    fun getWorklog(issue : String) : Call<Worklogs> {
        return jira.getWorklog(auth, issue)
    }

    fun addWorklog(issue : String, worklogData : WorklogTime) : Call<Worklog> {
        return jira.addWorklog(auth, issue, worklogData)
    }

    fun updateWorklog(issue : String, worklog: String, worklogData : WorklogTime) : Call<Worklog> {
        return jira.updateWorklog(auth, issue, worklog, worklogData)
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
data class Worklogs(val total : Int, val worklogs : List<Worklog>)
data class Worklog(val id : String, val started : String, val timeSpentSeconds : String, val author : User)
data class WorklogTime(val timeSpentSeconds : String)