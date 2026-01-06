package com.ora.app.core.error

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMapperTest {

    @Test
    fun `maps UnknownHostException to NoConnection`() {
        val result = ErrorMapper.map(UnknownHostException())
        assertThat(result).isEqualTo(AppError.Network.NoConnection)
    }

    @Test
    fun `maps SocketTimeoutException to Timeout`() {
        val result = ErrorMapper.map(SocketTimeoutException())
        assertThat(result).isEqualTo(AppError.Network.Timeout)
    }

    @Test
    fun `maps IOException to Network Unknown`() {
        val result = ErrorMapper.map(IOException("connection reset"))
        assertThat(result).isInstanceOf(AppError.Network.Unknown::class.java)
    }

    @Test
    fun `preserves AppError as-is`() {
        val error = AppError.Auth.InvalidCredentials
        val result = ErrorMapper.map(error)
        assertThat(result).isEqualTo(error)
    }

    @Test
    fun `maps unknown exception to Unknown`() {
        val result = ErrorMapper.map(RuntimeException("boom"))
        assertThat(result).isInstanceOf(AppError.Unknown::class.java)
    }
}
