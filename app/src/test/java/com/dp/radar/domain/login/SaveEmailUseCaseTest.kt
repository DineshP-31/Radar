package com.dp.radar.domain.login

import com.dp.radar.com.dp.radar.domain.login.SaveEmailUseCase
import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
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
        saveEmailUseCase = SaveEmailUseCase(
            loginRepository
        )
    }

    @Test
    fun `invoke should call repository_saveEmail with correct email`() {
        // ARRANGE
        val testEmail = "user.new@example.com"

        // ACT
        // Call the Use Case using the 'invoke' operator function
        saveEmailUseCase(testEmail)

        // ASSERT
        // Verify that the saveEmail method on the mock repository was called
        // exactly once with the provided email string.
        verify(loginRepository).saveEmail(testEmail)
    }
}