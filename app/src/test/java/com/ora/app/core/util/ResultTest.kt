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
    fun `getOrDefault returns default on error`() {
        val result: Result<String> = Result.error(AppError.Network.NoConnection)
        assertThat(result.getOrDefault("default")).isEqualTo("default")
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

    @Test
    fun `zip combines two successes`() {
        val a = Result.success(1)
        val b = Result.success("two")
        val zipped = a.zip(b)
        assertThat(zipped.getOrNull()).isEqualTo(1 to "two")
    }

    @Test
    fun `zip returns first error`() {
        val error = AppError.Network.NoConnection
        val a: Result<Int> = Result.error(error)
        val b = Result.success("two")
        val zipped = a.zip(b)
        assertThat(zipped.errorOrNull()).isEqualTo(error)
    }

    @Test
    fun `runCatching catches AppError`() {
        val result = runCatching { throw AppError.Auth.InvalidCredentials }
        assertThat(result.errorOrNull()).isEqualTo(AppError.Auth.InvalidCredentials)
    }

    @Test
    fun `runCatching wraps unknown exception`() {
        val result = runCatching { throw RuntimeException("boom") }
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Unknown::class.java)
    }
}
