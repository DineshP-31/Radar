import com.dp.radar.domain.GetUsersUseCase
import com.dp.radar.com.dp.radar.domain.model.User
import com.dp.radar.com.dp.radar.domain.repositories.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUsersUseCaseTest {

    // Mock the Use Case's dependency
    private lateinit var mockRepository: UserRepository

    // The Use Case instance to be tested
    private lateinit var getUsersUseCase: GetUsersUseCase

    @Before
    fun setup() {
        mockRepository = mock(UserRepository::class.java)
        getUsersUseCase = GetUsersUseCase(mockRepository)
    }

    // --- Scenario 1: Successful Data Fetch ---
    @Test
    fun `invoke should return success result when repository call succeeds`() = runTest {
        // ARRANGE
        val expectedUsers = listOf(
            User(
                1, "Alice", "alice@test.com", "Main St, City",
                cityAndStreet = ""
            ),
            User(
                2, "Bob", "bob@test.com", "Second Ave, Town",
                cityAndStreet = ""
            )
        )

        // Stub the repository to return a successful Result
        `when`(mockRepository.getUsers()).thenReturn(Result.success(expectedUsers))

        // ACT
        val result = getUsersUseCase()

        // ASSERT
        // 1. Verify the repository method was called
        verify(mockRepository).getUsers()

        // 2. Verify the result is a success and contains the correct data
        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
    }

    // --- Scenario 2: Data Fetch Failure ---
    @Test
    fun `invoke should return failure result when repository call fails`() = runTest {
        // ARRANGE
        val expectedException = RuntimeException("Network timeout")

        // Stub the repository to return a failed Result
        `when`(mockRepository.getUsers()).thenReturn(Result.failure(expectedException))

        // ACT
        val result = getUsersUseCase()

        // ASSERT
        // 1. Verify the repository method was called
        verify(mockRepository).getUsers()

        // 2. Verify the result is a failure and contains the correct exception
        assertTrue(result.isFailure)
        assertEquals(expectedException.message, result.exceptionOrNull()?.message)
    }
}