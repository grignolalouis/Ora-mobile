package com.ora.app.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

class DateTimeUtilTest {

    @Test
    fun `formatRelative returns A l'instant for recent times`() {
        val now = Instant.now().toString()
        val result = DateTimeUtil.formatRelative(now)
        assertThat(result).isEqualTo("A l'instant")
    }

    @Test
    fun `formatRelative returns minutes for times under an hour`() {
        val thirtyMinAgo = Instant.now().minusSeconds(30 * 60).toString()
        val result = DateTimeUtil.formatRelative(thirtyMinAgo)
        assertThat(result).contains("min")
    }

    @Test
    fun `formatRelative returns hours for times under a day`() {
        val twoHoursAgo = Instant.now().minusSeconds(2 * 60 * 60).toString()
        val result = DateTimeUtil.formatRelative(twoHoursAgo)
        assertThat(result).contains("h")
    }

    @Test
    fun `formatTime returns HH mm format`() {
        val time = "2024-01-15T14:30:00Z"
        val result = DateTimeUtil.formatTime(time)
        assertThat(result).matches("\\d{2}:\\d{2}")
    }

    @Test
    fun `parseIsoToMillis converts correctly`() {
        val iso = "2024-01-15T10:00:00Z"
        val millis = DateTimeUtil.parseIsoToMillis(iso)
        assertThat(millis).isGreaterThan(0)
    }
}
