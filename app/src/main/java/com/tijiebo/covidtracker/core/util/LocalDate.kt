package com.tijiebo.covidtracker.core.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDate.formatForNetwork(): String {
    return this.atStartOfDay().atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'"))
}

fun Date.formatToCalendarDate(): String {
    return SimpleDateFormat("dd MMM", Locale.getDefault()).format(this).toString()
}