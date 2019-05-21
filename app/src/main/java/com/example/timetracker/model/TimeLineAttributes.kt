package com.example.timetracker.model


//@Parcelize
class TimelineAttributes(
    var markerSize: Int,
    var markerColor: Int,
    var markerInCenter: Boolean,
    var linePadding: Int,
    var lineWidth: Int,
    var startLineColor: Int,
    var endLineColor: Int,
    var lineStyle: Int,
    var lineDashWidth: Int,
    var lineDashGap: Int
){  //): Parcelable {

    override fun toString(): String {
        return "TimelineAttributes(markerSize=$markerSize, markerColor=$markerColor, markerInCenter=$markerInCenter, linePadding=$linePadding, lineWidth=$lineWidth, startLineColor=$startLineColor, endLineColor=$endLineColor, lineStyle=$lineStyle, lineDashWidth=$lineDashWidth, lineDashGap=$lineDashGap)"
    }

    fun copy(): TimelineAttributes {
        val attributes = TimelineAttributes(markerSize, markerColor, markerInCenter, linePadding, lineWidth, startLineColor, endLineColor, lineStyle, lineDashWidth, lineDashGap)
        return attributes
    }
}