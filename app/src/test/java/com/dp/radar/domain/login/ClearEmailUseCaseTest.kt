package com.dp.radar.domain.login

import com.dp.radar.domain.repositories.ILoginRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.Test

class ClearEmailUseCaseTest {
    @Mock
    private lateinit var loginRepository: ILoginRepository
    private lateinit var clearEmailUseCase: ClearEmailUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        clearEmailUseCase = ClearEmailUseCase(loginRepository)
    }

    @Test
    fun `invoke should call repository clearEmail exactly once`() = runTest {
        clearEmailUseCase()

        verify(loginRepository, times(1)).clearEmail()
    }
}
