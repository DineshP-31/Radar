package com.dp.radar.domain.login

import com.dp.radar.com.dp.radar.domain.login.GetIsLoggedInUseCase
import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import kotlin.test.Test

class GetIsLoggedInUseCaseTest {
    @Mock
    private lateinit var loginRepository: ILoginRepository
    private lateinit var getIsLoggedInUseCase: GetIsLoggedInUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getIsLoggedInUseCase = GetIsLoggedInUseCase(
            loginRepository
        )
    }

    @Test
    fun `invoke should return TRUE when repository_isLoggedIn returns true`() {
        // ARRANGE
        // Stub the mock: tell it to return TRUE when isLoggedIn() is called
        `when`(loginRepository.isLoggedIn()).thenReturn(true)

        // ACT
        val result = getIsLoggedInUseCase()

        // ASSERT
        // 1. Verify that the result matches the stubbed value
        assertEquals(true, result)

        // 2. Verify that the repository method was called exactly once
        verify(loginRepository).isLoggedIn()
    }

    @Test
    fun `invoke should return FALSE when repository_isLoggedIn returns false`() {
        // ARRANGE
        // Stub the mock: tell it to return FALSE when isLoggedIn() is called
        `when`(loginRepository.isLoggedIn()).thenReturn(false)

        // ACT
        val result = getIsLoggedInUseCase()

        // ASSERT
        // 1. Verify that the result matches the stubbed value
        assertEquals(false, result)

        // 2. Verify that the repository method was called exactly once
        verify(loginRepository).isLoggedIn()
    }

}