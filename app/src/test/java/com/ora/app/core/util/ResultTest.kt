package com.ora.app.core.util

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import org.junit.Test

class ResultTest {

    @Test
    fun `success returns data`() {
        val result = Result.success("test")
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo("test")
    }

    @Test
    fun `error returns error`() {
        val error = AppError.Network.NoConnection
        val result = Result.error(error)
        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(error)
    }

    @Test
    fun `map transforms success value`() {
        val result = Result.success(5).map { it * 2 }
        assertThat(result.getOrNull()).isEqualTo(10)
    }

    @Test
    fun `map preserves error`() {
        val error = AppError.Network.Timeout
        val result: Result<Int> = Result.error(error)
        val mapped = result.map { it * 2 }
        assertThat(mapped.errorOrNull()).isEqualTo(error)
    }

    @Test
    fun `flatMap chains success`() {
        val result = Result.success(5).flatMap { Result.success(it * 2) }
        assertThat(result.getOrNull()).isEqualTo(10)
    }

    @Test
    fun `flatMap short-circuits on error`() {
        val error = AppError.Network.NoConnection
        val result: Result<Int> = Result.error(error)
        val chained = result.flatMap { Result.success(it * 2) }
        assertThat(chained.errorOrNull()).isEqualTo(error)
    }

    @Test
    fun `onSuccess is called for success`() {
        var called = false
        Result.success("test").onSuccess { called = true }
        assertThat(called).isTrue()
    }

    @Test
    fun `onError is called for error`() {
        var called = false
        Result.error(AppError.Network.NoConnection).onError { called = true }
        assertThat(called).isTrue()
    }

}
