package com.example.timetracker.utils

import android.text.format.DateUtils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateTimeUtils {
    fun parseSeconds(time : String) : String{
        return parseSeconds(Integer.parseInt(time))
    }

    fun parseSeconds(time : Int) : String {
        return Math.abs(time/60*60).toString() + "h"
    }

    fun parseDateTime(dateString: String, originalFormat: String, outputFromat: String): String {

        val formatter = SimpleDateFormat(originalFormat, Locale.US)
        var date: Date? = null
        return try {
            date = formatter.parse(dateString)

            val dateFormat = SimpleDateFormat(outputFromat, Locale("US"))

            dateFormat.format(date)

        } catch (e: ParseException) {
            e.printStackTrace().toString()
        }

    }

    fun getRelativeTimeSpan(dateString: String, originalFormat: String): String {

        val formatter = SimpleDateFormat(originalFormat, Locale.US)
        var date: Date? = null
        return try {
            date = formatter.parse(dateString)

            DateUtils.getRelativeTimeSpanString(date!!.time).toString()

        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }

    }
}
