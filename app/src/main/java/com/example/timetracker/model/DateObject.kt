package com.example.timetracker.model
import java.text.SimpleDateFormat
import java.util.*

data class DateObject(val date : Date) {
    constructor(dateString : String) : this(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(dateString))
    fun format(pattern : String) = SimpleDateFormat(pattern).format(date)
    val stringDate = SimpleDateFormat("yyyy/MM/dd").format(date)
    val stringShortDate = SimpleDateFormat("EEE dd/MM").format(date)
}