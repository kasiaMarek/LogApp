package com.example.timetracker.utils

import com.example.timetracker.R
import android.content.Context
import android.widget.Toast


object ErrorUtils {
    fun unexpected(context: Context) {
        Toast.makeText(context, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
    }
}