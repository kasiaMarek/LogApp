package com.example.timetracker.jiraservice

import android.util.Base64
import android.util.Log

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface JiraApi {
    @Headers("Content-Type: application/json")
    @GET("myself")
    fun myself(@Header("Authorization") token : String) : Call<User>

}

class JiraService(login : String, token : String) {
    val auth = "Basic " + Base64.encodeToString(("$login:$token").toByteArray(), Base64.NO_WRAP)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://logapp.atlassian.net/rest/api/2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val jira = retrofit.create(JiraApi::class.java)

    fun tryMyself() : Call<User> {
        return jira.myself(auth)
    }
}

data class User(val displayName:String)