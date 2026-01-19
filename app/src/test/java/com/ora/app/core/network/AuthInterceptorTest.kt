package com.ora.app.core.network

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.storage.TokenManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var cookieJar: CookieJar
    private lateinit var interceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain

    @Before
    fun setup() {
        tokenManager = mockk(relaxed = true)
        cookieJar = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {}
            override fun loadForRequest(url: HttpUrl): List<Cookie> = emptyList()
        }
        interceptor = AuthInterceptor(tokenManager, mockk(relaxed = true))
        chain = mockk(relaxed = true)
    }

    private fun createRequest(path: String): Request {
        return Request.Builder()
            .url("https://api.example.com/api/v1/$path")
            .build()
    }

    private fun createResponse(request: Request, code: Int, body: String = ""): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("OK")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }

    @Test
    fun `adds authorization header with valid token`() {
        val request = createRequest("sessions")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token_123"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == "Bearer test_token_123"
            })
        }
    }

    @Test
    fun `skips auth header when token is null`() {
        val request = createRequest("sessions")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns null
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `skips auth for login endpoint`() {
        val request = createRequest("auth/login")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        val result = interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `skips auth for register endpoint`() {
        val request = createRequest("auth/register")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `skips auth for forgot-password endpoint`() {
        val request = createRequest("auth/forgot-password")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `skips auth for reset-password endpoint`() {
        val request = createRequest("auth/reset-password")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `skips auth for verify-email endpoint`() {
        val request = createRequest("auth/verify-email")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `skips auth for refresh-tokens endpoint`() {
        val request = createRequest("auth/refresh-tokens")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `non-401 errors pass through`() {
        val request = createRequest("sessions")
        val response = createResponse(request, 500, """{"error": "Server error"}""")

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        val result = interceptor.intercept(chain)

        assertThat(result.code).isEqualTo(500)
    }

    @Test
    fun `200 response passes through unchanged`() {
        val request = createRequest("sessions")
        val response = createResponse(request, 200, """{"data": []}""")

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        val result = interceptor.intercept(chain)

        assertThat(result.code).isEqualTo(200)
    }

    @Test
    fun `public endpoint path detection for nested paths`() {
        val request = Request.Builder()
            .url("https://api.example.com/api/v1/auth/login/some/extra/path")
            .build()
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "test_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `non-public endpoint requires auth`() {
        val request = createRequest("users/me")
        val response = createResponse(request, 200)

        every { tokenManager.accessToken } returns "my_token"
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == "Bearer my_token"
            })
        }
    }
}
