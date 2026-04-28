package com.dp.radar.domain.login

import com.dp.radar.com.dp.radar.domain.login.SaveEmailUseCase
import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class SaveEmailUseCaseTest {
    @Mock
    private lateinit var loginRepository: ILoginRepository
    private lateinit var saveEmailUseCase: SaveEmailUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        saveEmailUseCase = SaveEmailUseCase(loginRepository)
    }

    @Test
    fun `invoke should call repository saveEmail with correct email`() = runTest {
        val testEmail = "user.new@example.com"

        saveEmailUseCase(testEmail)

        verify(loginRepository).saveEmail(testEmail)
    }
}