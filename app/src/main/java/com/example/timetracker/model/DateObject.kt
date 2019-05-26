package com.example.timetracker.model
import java.text.SimpleDateFormat
import java.util.*

data class DateObject(val date : Date) {
    constructor(dateString : String) : this(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(dateString))
    fun format(pattern : String) = SimpleDateFormat(pattern).format(date)
    val stringDate = format("yyyy/MM/dd")
    val stringDateWithTime = format("yyyy/MM/dd HH:mm")
    val stringFullDate = format("HH:mm dd/MM/yyyy")
    val stringShortDate = format("EEE dd/MM")
}