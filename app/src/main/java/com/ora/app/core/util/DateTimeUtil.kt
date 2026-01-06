package com.ora.app.core.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateTimeUtil {

    fun formatRelative(isoString: String): String {
        val instant = Instant.parse(isoString)
        val now = Instant.now()
        val minutes = ChronoUnit.MINUTES.between(instant, now)

        return when {
            minutes < 1 -> "A l'instant"
            minutes < 60 -> "Il y a $minutes min"
            minutes < 1440 -> "Il y a ${minutes / 60}h"
            minutes < 2880 -> "Hier"
            minutes < 10080 -> "Il y a ${minutes / 1440} jours"
            else -> formatDate(instant)
        }
    }

    fun formatDate(instant: Instant): String {
        val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDate.format(DateTimeFormatter.ofPattern("d MMM"))
    }

    fun formatTime(isoString: String): String {
        val instant = Instant.parse(isoString)
        val localTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun parseIsoToMillis(isoString: String): Long = Instant.parse(isoString).toEpochMilli()
}
