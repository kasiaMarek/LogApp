package com.example.timetracker.model

import kotlin.math.ceil
import kotlin.math.floor

data class TimeObject(val seconds : Int) {
    constructor(secondsString: String) : this(Integer.parseInt(secondsString))

    val minutes : Float = seconds/60f
    val hours : Float = minutes/60f
    val intMinutes : Int = ceil(minutes).toInt()
    val intHours : Int = floor(hours).toInt()
    val hoursAndMinutes : Pair<Int, Int> = Pair(intHours, ceil(intMinutes - intHours * 60.0).toInt())
    val string =  "${hoursAndMinutes.first}h ${hoursAndMinutes.second}min"
}