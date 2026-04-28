package com.dp.radar.data.repositories.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class LoginRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: LoginRepository

    @Before
    fun setup() {
        val testFile = File.createTempFile("test_prefs_${System.nanoTime()}", ".preferences_pb")
        testFile.deleteOnExit()
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { testFile }
        )
        repository = LoginRepository(dataStore)
    }

    @Test
    fun `isLoggedIn emits false when no email is saved`() = runTest(testDispatcher) {
        repository.isLoggedIn.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveEmail causes isLoggedIn to emit true`() = runTest(testDispatcher) {
        repository.saveEmail("test@example.com")
        repository.isLoggedIn.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearEmail causes isLoggedIn to emit false`() = runTest(testDispatcher) {
        repository.saveEmail("test@example.com")
        repository.clearEmail()
        repository.isLoggedIn.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveUserId and userId emit correct value`() = runTest(testDispatcher) {
        repository.saveUserId(42L)
        repository.userId.test {
            assertEquals(42L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `userId emits -1 when no userId is saved`() = runTest(testDispatcher) {
        repository.userId.test {
            assertEquals(-1L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
