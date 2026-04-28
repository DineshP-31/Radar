package com.dp.radar.domain.login

import com.dp.radar.com.dp.radar.domain.login.GetIsLoggedInUseCase
import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
        getIsLoggedInUseCase = GetIsLoggedInUseCase(loginRepository)
    }

    @Test
    fun `invoke should emit TRUE when repository isLoggedIn emits true`() = runTest {
        `when`(loginRepository.isLoggedIn).thenReturn(flowOf(true))

        val result = getIsLoggedInUseCase().first()

        assertEquals(true, result)
        verify(loginRepository).isLoggedIn
    }

    @Test
    fun `invoke should emit FALSE when repository isLoggedIn emits false`() = runTest {
        `when`(loginRepository.isLoggedIn).thenReturn(flowOf(false))

        val result = getIsLoggedInUseCase().first()

        assertEquals(false, result)
        verify(loginRepository).isLoggedIn
    }
}