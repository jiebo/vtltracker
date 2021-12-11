package com.tijiebo.covidtracker.core.util

fun Double.to2dp() : String {
    return String.format("%.2f", this)
}

fun Double.to3dp() : String {
    return String.format("%.3f", this)
}

fun Int.formatString(): String {
    return String.format("%,d", this)
}