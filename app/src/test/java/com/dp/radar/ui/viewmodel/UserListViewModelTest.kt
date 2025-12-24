package com.dp.radar.ui.viewmodel

import app.cash.turbine.test
import com.dp.radar.domain.GetUsersUseCase
import com.dp.radar.com.dp.radar.domain.model.User
import com.dp.radar.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@ExperimentalCoroutinesApi
class UserListViewModelTest {

    // Rule to swap the main dispatcher with a test dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockGetUsersUseCase: GetUsersUseCase
    private lateinit var viewModel: UserListViewModel
    private val fakeUsers = listOf(User(1, "A", "a", "a@t.com", ""))

    @Before
    fun setup() {
        mockGetUsersUseCase = mock(GetUsersUseCase::class.java)
    }

    @Test
    fun `Successful load should emit Loading then Success state`() = runTest {
        // ARRANGE: Stub Use Case to return success
        whenever(mockGetUsersUseCase.invoke()).thenReturn(Result.success(fakeUsers))
        viewModel = UserListViewModel(mockGetUsersUseCase, StandardTestDispatcher())

        viewModel.state.test {
            // 1. Initial State
            // The VM is initialized with isLoading=true and sends LoadUsers,
            // so the first two states are often initial default (if set) and then loading.
            val initial = awaitItem()
            assertEquals(false, initial.isLoading, "Initial state should be non-loading (from companion object)")

            // 2. Loading State (set by loadUsers() function)
            val loading = awaitItem()
            assertTrue(loading.isLoading, "Second state should be Loading")

            // 3. Success State (after use case returns)
            val success = awaitItem()
            assertEquals(false, success.isLoading, "Final state should not be loading")
            assertEquals(fakeUsers, success.users, "Should contain the fetched user list")
            assertEquals(null, success.error, "Error must be null")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Load failure should emit Loading then Error state`() = runTest {
        // ARRANGE: Stub Use Case to return failure
        val errorMessage = "Network timeout"
        `when`(mockGetUsersUseCase.invoke()).thenReturn(Result.failure(RuntimeException(errorMessage)))

        // ACT: Initialize VM (triggers load in init block)
        viewModel = UserListViewModel(mockGetUsersUseCase, StandardTestDispatcher())

        viewModel.state.test {
            // 1. Initial State
            awaitItem() // Ignore initial state

            // 2. Loading State
            val loading = awaitItem()
            assertTrue(loading.isLoading, "State should transition to Loading")

            // 3. Error State
            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading, "Final state should not be loading")
            assertTrue(errorState.users.isEmpty(), "User list should be empty on error")
            assertTrue(errorState.error!!.contains(errorMessage), "Error message should be present")

            cancelAndIgnoreRemainingEvents()
        }
    }
}