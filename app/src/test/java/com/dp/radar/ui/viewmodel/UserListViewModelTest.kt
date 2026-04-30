package com.dp.radar.ui.viewmodel

import app.cash.turbine.test
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.GetUsersUseCase
import com.dp.radar.domain.ObserveUsersUseCase
import com.dp.radar.domain.login.GetUserIdUseCase
import com.dp.radar.domain.model.User
import com.dp.radar.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class UserListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockGetUsersUseCase: GetUsersUseCase
    private lateinit var mockObserveUsersUseCase: ObserveUsersUseCase
    private lateinit var mockGetUserIdUseCase: GetUserIdUseCase
    private lateinit var viewModel: UserListViewModel

    private val fakeUsers = listOf(User(1L, "A", "a@t.com", latLong = LatLong(0.0, 0.0)))

    @Before
    fun setup() {
        mockGetUsersUseCase = mock()
        mockObserveUsersUseCase = mock()
        mockGetUserIdUseCase = mock()
        whenever(mockGetUserIdUseCase.invoke()).thenReturn(flowOf(0L))
    }

    @Test
    fun `Successful load should emit Loading then Success state`() = runTest {
        whenever(mockGetUsersUseCase.invoke()).thenReturn(ApiResult.Success(fakeUsers))
        whenever(mockObserveUsersUseCase.invoke()).thenReturn(flowOf(fakeUsers))
        viewModel = UserListViewModel(
            mockGetUsersUseCase,
            mockObserveUsersUseCase,
            mockGetUserIdUseCase,
            StandardTestDispatcher(testScheduler),
        )

        viewModel.state.test {
            assertEquals(false, awaitItem().isLoading) // initial

            // startObservingUsers populates users from Room cache before network fires
            val withUsers = awaitItem()
            assertEquals(fakeUsers, withUsers.users)
            assertEquals(false, withUsers.isLoading)

            val loading = awaitItem() // refreshFromNetwork sets isLoading = true
            assertTrue(loading.isLoading, "Should transition to Loading")
            assertEquals(fakeUsers, loading.users)

            val success = awaitItem() // refreshFromNetwork completes successfully
            assertEquals(false, success.isLoading, "Final state should not be loading")
            assertEquals(fakeUsers, success.users, "Should contain the fetched user list")
            assertEquals(null, success.error, "Error must be null")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Load failure should emit Loading then Error state`() = runTest {
        val errorMessage = "Network timeout"
        whenever(mockGetUsersUseCase.invoke()).thenReturn(ApiResult.Error(errorMessage))
        // Empty Room cache — startObservingUsers emits same state as initial, deduplicated
        whenever(mockObserveUsersUseCase.invoke()).thenReturn(flowOf(emptyList()))
        viewModel = UserListViewModel(
            mockGetUsersUseCase,
            mockObserveUsersUseCase,
            mockGetUserIdUseCase,
            StandardTestDispatcher(testScheduler),
        )

        viewModel.state.test {
            awaitItem() // initial

            val loading = awaitItem()
            assertTrue(loading.isLoading, "State should transition to Loading")

            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading, "Final state should not be loading")
            assertTrue(errorState.users.isEmpty(), "User list should be empty on error")
            assertTrue(errorState.error!!.contains(errorMessage), "Error message should be present")

            cancelAndIgnoreRemainingEvents()
        }
    }
}
