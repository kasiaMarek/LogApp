package com.example.timetracker

import android.content.Context
import android.content.SharedPreferences
import com.example.timetracker.jiraservice.User
import retrofit2.Callback

const val PREFS_FILENAME = "com.timetracker"
const val PASS = "password"
const val MAIL = "mail"
const val PROJECT = "project"
class Storage (context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    private var password: String?
        get() = prefs.getString(PASS, null)
        set(value) = prefs.edit().putString(PASS, value).apply()

    private var mail: String?
        get() = prefs.getString(MAIL, null)
        set(value) = prefs.edit().putString(MAIL, value).apply()

    private var project: String?
        get() = prefs.getString(PROJECT, null)
        set(value) = prefs.edit().putString(PROJECT, value).apply()

    fun writeCredentials(credentials: Credentials) {
        this.password = credentials.token
        this.mail = credentials.login
        this.project = credentials.projectName
    }

    fun getCredentials() : Credentials {
        return Credentials(mail, password, project)
    }
}

data class Credentials(val login : String?, val token : String?, val projectName : String?) {
    fun checkIfInit() : Boolean {
        return login != null && token != null && projectName != null
    }
}