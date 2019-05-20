package com.example.timetracker

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


const val PREFS_FILENAME = "com.timetracker"
const val TODOS_DATA_FIELD = "credentials"
class Storage (context: Context) {
    private val moshi: Moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(Credentials::class.java)!!
    private val adapter: JsonAdapter<Credentials> = moshi.adapter(type)
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    private var credentials: String
        get() = prefs.getString(TODOS_DATA_FIELD, adapter.toJson(Credentials(null, null, null)))!!
        set(value) = prefs.edit().putString(TODOS_DATA_FIELD, value).apply()

    fun writeCredentials(credentials: Credentials) {
        this.credentials = adapter.toJson(credentials)
    }

    fun getCredentials() : Credentials {
        return adapter.fromJson(credentials)!!
    }
}

data class Credentials(val login : String?, val token : String?, val projectName : String?) {
    fun checkIfInit() : Boolean {
        return login != null && token != null && projectName != null
    }
}