package com.example.timetracker.jiraservice

import android.util.Base64
import android.webkit.URLUtil
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface JiraApi {
    @Headers("Content-Type: application/json")
    @GET("myself")
    fun myself(@Header("Authorization") token : String) : Call<User>

    @Headers("Content-Type: application/json")
    @GET("search")
    fun tasks(@Header("Authorization") token : String, @Query("jql") assignee : String) : Call<Tasks>

    @Headers("Content-Type: application/json")
    @GET("issue/{issue}/worklog")
    fun getWorklog(@Header("Authorization") token : String, @Path("issue") issue : String) : Call<Worklogs>

    @Headers("Content-Type: application/json")
    @POST("issue/{issue}/worklog")
    fun addWorklog(@Header("Authorization") token : String, @Path("issue") issue : String, @Body worklogData : WorklogTime) : Call<Worklog>

    @Headers("Content-Type: application/json")
    @PUT("issue/{issue}/worklog/{worklog}")
    fun updateWorklog(@Header("Authorization") token : String, @Path("issue") issue : String, @Path("worklog") worklog : String, @Body worklogData : WorklogTime) : Call<Worklog>
}

class JiraService(login : String, token : String, url : String) {
    val auth = "Basic " + Base64.encodeToString(("$login:$token").toByteArray(), Base64.NO_WRAP)
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val jira = retrofit.create(JiraApi::class.java)
    var name : String? = null

    fun tryMyself() : Call<User> {
        return jira.myself(auth)
    }

    fun getTasks() : Call<Tasks>? {
        return if (name != null) jira.tasks(auth, "assignee=$name") else null
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
    lateinit var jira : JiraService

    fun initJira(login : String, token : String, projectName : String): Boolean {
        val pattern = Regex("[a-zA-Z0-9]*")
        if(projectName != "" && pattern.matches(projectName)) {
            val url = "https://$projectName.atlassian.net/rest/api/3/"
            if (URLUtil.isValidUrl(url)) {
                jira = JiraService(login, token, url)
                return true
            }
        }
        return false
    }
}

data class User(val displayName:String)

data class Tasks(val total : Int, val issues : List<Issue>)
data class Issue(val id : String, val key : String, val fields : Fields)
data class Fields(val summary : String, val description : String?, val timespent : String?, val created : String?)
data class Worklogs(val total : Int, val worklogs : List<Worklog>)
data class Worklog(val id : String, val started : String, val timeSpentSeconds : String, val author : User)
data class WorklogTime(val timeSpentSeconds : String)