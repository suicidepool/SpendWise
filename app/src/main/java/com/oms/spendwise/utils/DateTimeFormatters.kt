package com.oms.spendwise.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate(localDate: LocalDate): String{
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return localDate.format(formatter)
}

fun formatTime(localTime: LocalTime): String{
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    return localTime.format(formatter)
}

fun formatDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern(
        "MMMM dd, yyyy • hh:mm a",
        Locale.getDefault()
    )
    return dateTime.format(formatter)
}