package com.tijiebo.covidtracker.core.util

fun Double.to2dp() : String {
    return String.format("%.2f", this)
}

fun Int.formatString(): String {
    return String.format("%,d", this)
}