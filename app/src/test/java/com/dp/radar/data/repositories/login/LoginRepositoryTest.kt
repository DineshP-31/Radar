package com.dp.radar.data.repositories.login

import android.content.SharedPreferences
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class LoginRepositoryTest {

    // Use Mockito's @Mock annotation (requires MockitoJUnitRunner, or setup in @Before)
    private lateinit var mockSharedPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    // The repository instance to be tested
    private lateinit var repository: LoginRepository

    private val KEY_EMAIL = "user_email"

    @Before
    fun setup() {
        // Initialize the mocks
        mockSharedPrefs = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)

        // Stubbing the chain: sharedPrefs.edit() returns the editor
        `when`(mockSharedPrefs.edit()).thenReturn(mockEditor)

        // Stubbing the editor chain: editor.putString/remove returns the editor itself
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.remove(anyString())).thenReturn(mockEditor)

        // Stubbing the final commit/apply call. The sharedPrefs.edit { ... } uses apply()
        `when`(mockEditor.apply()).thenAnswer { } // Mocking void method

        // Initialize the repository
        repository = LoginRepository(mockSharedPrefs)
    }

    @Test
    fun `saveEmail should call putString with correct key and value`() {
        // ARRANGE
        val testEmail = "test@example.com"

        // ACT
        repository.saveEmail(testEmail)

        // ASSERT
        // Verify that putString was called on the editor with the correct key and email
        verify(mockEditor, times(1)).putString(KEY_EMAIL, testEmail)

        // Verify that apply() was called
        verify(mockEditor, times(1)).apply()
    }

    @Test
    fun `clearEmail should call remove with correct key`() {
        // ACT
        repository.clearEmail()

        // ASSERT
        // Verify that remove was called on the editor with the correct key
        verify(mockEditor, times(1)).remove(KEY_EMAIL)

        // Verify that apply() was called
        verify(mockEditor, times(1)).apply()
    }

    @Test
    fun `isLoggedIn should return true when email is present`() {
        // ARRANGE
        val savedEmail = "user@test.com"

        // Stubbing: When asked for KEY_EMAIL, return the saved email string
        `when`(mockSharedPrefs.getString(KEY_EMAIL, null)).thenReturn(savedEmail)

        // ACT
        val result = repository.isLoggedIn()

        // ASSERT
        // Verify that getString was called
        verify(mockSharedPrefs, times(1)).getString(KEY_EMAIL, null)

        // Verify the result is true
        assertEquals(true, result)
    }

    @Test
    fun `isLoggedIn should return false when email is not present`() {
        // ARRANGE
        // Stubbing: When asked for KEY_EMAIL, return null
        `when`(mockSharedPrefs.getString(KEY_EMAIL, null)).thenReturn(null)

        // ACT
        val result = repository.isLoggedIn()

        // ASSERT
        // Verify that getString was called
        verify(mockSharedPrefs, times(1)).getString(KEY_EMAIL, null)

        // Verify the result is false
        assertEquals(false, result)
    }
}
