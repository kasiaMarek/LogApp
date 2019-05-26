package com.example.timetracker.model

import kotlin.math.ceil
import kotlin.math.floor

data class TimeObject(val seconds : Int) {
    constructor(secondsString: String) : this(Integer.parseInt(secondsString))
    constructor(secondsAndMinutes: Pair<Int, Int>) : this((secondsAndMinutes.first*60+secondsAndMinutes.second)*60)

    val minutes : Float = seconds/60f
    val hours : Float = minutes/60f
    val intMinutes : Int = ceil(minutes).toInt()
    val intHours : Int = floor(hours).toInt()
    val hoursAndMinutes : Pair<Int, Int> = Pair(intHours, ceil(intMinutes - intHours * 60.0).toInt())
    val string =  "${hoursAndMinutes.first}h ${hoursAndMinutes.second}min"
    val min_string = when {
        intMinutes == 0 && intHours == 0 -> ""
        intMinutes == 0 && intHours != 0 -> "${hoursAndMinutes.first}h"
        intMinutes != 0 && intHours == 0 -> "${hoursAndMinutes.second}min"
        else -> string
    }

}