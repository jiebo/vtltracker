package com.tijiebo.covidtracker.core.util

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun LocalDate.formatForNetwork(): String {
    return this.atStartOfDay().atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'"))
}