package com.ora.app.presentation.features.chat

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.Agent
import com.ora.app.domain.model.FeedbackState
import com.ora.app.domain.model.Interaction
import com.ora.app.domain.model.InteractionStatus
import com.ora.app.domain.model.Message
import com.ora.app.domain.model.Session
import com.ora.app.domain.model.SessionDetail
import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.model.User
import com.ora.app.domain.usecase.agent.GetAgentsUseCase
import com.ora.app.domain.usecase.auth.GetCurrentUserUseCase
import com.ora.app.domain.usecase.auth.LogoutUseCase
import com.ora.app.domain.usecase.session.CreateSessionUseCase
import com.ora.app.domain.usecase.session.DeleteSessionUseCase
import com.ora.app.domain.usecase.session.GetSessionHistoryUseCase
import com.ora.app.domain.usecase.session.GetSessionsUseCase
import com.ora.app.domain.usecase.session.SendMessageUseCase
import com.ora.app.domain.usecase.session.StreamResponseUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private lateinit var viewModel: ChatViewModel
    private lateinit var getAgentsUseCase: GetAgentsUseCase
    private lateinit var getSessionsUseCase: GetSessionsUseCase
    private lateinit var createSessionUseCase: CreateSessionUseCase
    private lateinit var getSessionHistoryUseCase: GetSessionHistoryUseCase
    private lateinit var deleteSessionUseCase: DeleteSessionUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var streamResponseUseCase: StreamResponseUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockUser = User(
        id = "user_123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = true,
        profilePictureUrl = null
    )

    private val mockAgents = listOf(
        Agent(
            type = "assistant",
            name = "Assistant",
            description = "General assistant",
            greeting = "Hello!",
            version = "1.0.0",
            capabilities = listOf("chat"),
            icon = "assistant_icon"
        )
    )

    private val mockSession = Session(
        id = "session_1",
        userId = "user_123",
        agentType = "assistant",
        title = "Test Chat",
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
        messageCount = 0,
        metadata = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAgentsUseCase = mockk()
        getSessionsUseCase = mockk()
        createSessionUseCase = mockk()
        getSessionHistoryUseCase = mockk()
        deleteSessionUseCase = mockk()
        sendMessageUseCase = mockk()
        streamResponseUseCase = mockk()
        logoutUseCase = mockk()
        getCurrentUserUseCase = mockk()

        viewModel = ChatViewModel(
            getAgentsUseCase,
            getSessionsUseCase,
            createSessionUseCase,
            getSessionHistoryUseCase,
            deleteSessionUseCase,
            sendMessageUseCase,
            streamResponseUseCase,
            logoutUseCase,
            getCurrentUserUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser updates state`() = runTest {
        coEvery { getCurrentUserUseCase() } returns Result.success(mockUser)

        viewModel.dispatch(ChatIntent.LoadUser)

        assertThat(viewModel.state.value.user).isEqualTo(mockUser)
    }

    @Test
    fun `loadAgents sets agents`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(emptyList())

        viewModel.dispatch(ChatIntent.LoadAgents)

        assertThat(viewModel.state.value.agents).isEqualTo(mockAgents)
        assertThat(viewModel.state.value.isLoadingAgents).isFalse()
    }

    @Test
    fun `loadAgents auto-selects first agent`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase("assistant") } returns Result.success(emptyList())

        viewModel.dispatch(ChatIntent.LoadAgents)

        assertThat(viewModel.state.value.selectedAgent).isEqualTo(mockAgents.first())
    }

    @Test
    fun `loadAgents handles error`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.error(AppError.Network.NoConnection)

        viewModel.dispatch(ChatIntent.LoadAgents)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(viewModel.state.value.isLoadingAgents).isFalse()
    }

    @Test
    fun `selectAgent triggers session load`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase("assistant") } returns Result.success(listOf(mockSession))

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()

        assertThat(viewModel.state.value.sessions).hasSize(1)
    }

    @Test
    fun `updateInput updates state`() = runTest {
        viewModel.dispatch(ChatIntent.UpdateInput("Hello"))

        assertThat(viewModel.state.value.inputText).isEqualTo("Hello")
    }

    @Test
    fun `toggleDrawer toggles state`() = runTest {
        assertThat(viewModel.state.value.isDrawerOpen).isFalse()

        viewModel.dispatch(ChatIntent.ToggleDrawer)
        assertThat(viewModel.state.value.isDrawerOpen).isTrue()

        viewModel.dispatch(ChatIntent.ToggleDrawer)
        assertThat(viewModel.state.value.isDrawerOpen).isFalse()
    }

    @Test
    fun `closeDrawer sets drawer closed`() = runTest {
        viewModel.dispatch(ChatIntent.ToggleDrawer)
        assertThat(viewModel.state.value.isDrawerOpen).isTrue()

        viewModel.dispatch(ChatIntent.CloseDrawer)
        assertThat(viewModel.state.value.isDrawerOpen).isFalse()
    }

    @Test
    fun `updateSearchQuery updates state`() = runTest {
        viewModel.dispatch(ChatIntent.UpdateSearchQuery("test"))

        assertThat(viewModel.state.value.sessionSearchQuery).isEqualTo("test")
    }

    @Test
    fun `dismissError clears error`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.error(AppError.Network.NoConnection)
        viewModel.dispatch(ChatIntent.LoadAgents)

        assertThat(viewModel.state.value.error).isNotNull()

        viewModel.dispatch(ChatIntent.DismissError)
        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun `logout triggers navigation`() = runTest {
        coEvery { logoutUseCase() } returns Result.success(Unit)

        viewModel.effect.test {
            viewModel.dispatch(ChatIntent.Logout)
            assertThat(awaitItem()).isEqualTo(ChatEffect.NavigateToLogin)
        }
    }

    @Test
    fun `newChat clears state`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(listOf(mockSession))
        coEvery { getSessionHistoryUseCase(any(), any()) } returns Result.success(
            SessionDetail(mockSession, emptyList())
        )

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()
        viewModel.dispatch(ChatIntent.SelectSession("session_1"))
        advanceUntilIdle()
        viewModel.dispatch(ChatIntent.NewChat)

        assertThat(viewModel.state.value.activeSessionId).isNull()
        assertThat(viewModel.state.value.interactions).isEmpty()
        assertThat(viewModel.state.value.inputText).isEmpty()
    }

    @Test
    fun `selectSession loads history`() = runTest {
        val mockHistory = listOf(
            Message("user", "Hello", "2024-01-01T00:00:00Z", null),
            Message("assistant", "Hi!", "2024-01-01T00:00:01Z", null)
        )
        val mockDetail = SessionDetail(mockSession, mockHistory)

        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(listOf(mockSession))
        coEvery { getSessionHistoryUseCase("assistant", "session_1") } returns Result.success(mockDetail)

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()
        viewModel.dispatch(ChatIntent.SelectSession("session_1"))
        advanceUntilIdle()

        assertThat(viewModel.state.value.activeSessionId).isEqualTo("session_1")
        assertThat(viewModel.state.value.interactions).isNotEmpty()
    }

    @Test
    fun `canSend returns true when input not blank and not sending`() = runTest {
        viewModel.dispatch(ChatIntent.UpdateInput("Hello"))

        assertThat(viewModel.state.value.canSend).isTrue()
    }

    @Test
    fun `canSend returns false when input is blank`() = runTest {
        viewModel.dispatch(ChatIntent.UpdateInput(""))

        assertThat(viewModel.state.value.canSend).isFalse()
    }

    @Test
    fun `isWelcomeScreen returns true when no session and no interactions`() = runTest {
        assertThat(viewModel.state.value.isWelcomeScreen).isTrue()
    }

    @Test
    fun `filteredSessions returns all when search is blank`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(listOf(mockSession))

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()

        assertThat(viewModel.state.value.filteredSessions).hasSize(1)
    }

    @Test
    fun `filteredSessions filters by search query`() = runTest {
        val sessions = listOf(
            mockSession.copy(id = "1", title = "First Chat"),
            mockSession.copy(id = "2", title = "Second Chat"),
            mockSession.copy(id = "3", title = "Another")
        )
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(sessions)

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()
        viewModel.dispatch(ChatIntent.UpdateSearchQuery("Chat"))

        assertThat(viewModel.state.value.filteredSessions).hasSize(2)
    }

    @Test
    fun `setFeedback toggles feedback state`() = runTest {
        val interaction = Interaction(
            userMessage = "Hello",
            assistantResponse = "Hi!",
            status = InteractionStatus.COMPLETED,
            feedbackState = FeedbackState.NONE
        )
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(listOf(mockSession))
        coEvery { getSessionHistoryUseCase(any(), any()) } returns Result.success(
            SessionDetail(mockSession, listOf(
                Message("user", "Hello", "2024-01-01T00:00:00Z", null),
                Message("assistant", "Hi!", "2024-01-01T00:00:01Z", null)
            ))
        )

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()
        viewModel.dispatch(ChatIntent.SelectSession("session_1"))
        advanceUntilIdle()

        viewModel.dispatch(ChatIntent.SetFeedback(0, FeedbackState.POSITIVE))

        assertThat(viewModel.state.value.interactions[0].feedbackState).isEqualTo(FeedbackState.POSITIVE)

        viewModel.dispatch(ChatIntent.SetFeedback(0, FeedbackState.POSITIVE))
        assertThat(viewModel.state.value.interactions[0].feedbackState).isEqualTo(FeedbackState.NONE)
    }

    @Test
    fun `copyMessage sends effect`() = runTest {
        viewModel.effect.test {
            viewModel.dispatch(ChatIntent.CopyMessage("Test content"))
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(ChatEffect.CopiedToClipboard::class.java)
            assertThat((effect as ChatEffect.CopiedToClipboard).message).isEqualTo("Test content")
        }
    }

    @Test
    fun `deleteSession removes from list`() = runTest {
        coEvery { getAgentsUseCase() } returns Result.success(mockAgents)
        coEvery { getSessionsUseCase(any()) } returns Result.success(listOf(mockSession))
        coEvery { deleteSessionUseCase("assistant", "session_1") } returns Result.success(Unit)

        viewModel.dispatch(ChatIntent.LoadAgents)
        advanceUntilIdle()

        assertThat(viewModel.state.value.sessions).hasSize(1)

        viewModel.dispatch(ChatIntent.DeleteSession("session_1"))
        advanceUntilIdle()

        assertThat(viewModel.state.value.sessions).isEmpty()
    }
}
