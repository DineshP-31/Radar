package com.dp.radar.domain

import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.model.User
import com.dp.radar.domain.repositories.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUsersUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getUsersUseCase: GetUsersUseCase

    @Before
    fun setup() {
        mockRepository = mock()
        getUsersUseCase = GetUsersUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success result when repository call succeeds`() = runTest {
        val expectedUsers = listOf(
            User(1L, "Alice", "alice@test.com", latLong = LatLong(0.0, 0.0)),
            User(2L, "Bob", "bob@test.com", latLong = LatLong(0.0, 0.0)),
        )

        whenever(mockRepository.getUsers()).thenReturn(ApiResult.Success(expectedUsers))

        val result = getUsersUseCase()

        verify(mockRepository).getUsers()
        assertTrue(result is ApiResult.Success)
        assertEquals(expectedUsers, result.data)
    }

    @Test
    fun `invoke should return failure result when repository call fails`() = runTest {
        val errorMessage = "Network timeout"

        whenever(mockRepository.getUsers()).thenReturn(ApiResult.Error(errorMessage))

        val result = getUsersUseCase()

        verify(mockRepository).getUsers()
        assertTrue(result is ApiResult.Error)
        assertEquals(errorMessage, result.message)
    }
}
