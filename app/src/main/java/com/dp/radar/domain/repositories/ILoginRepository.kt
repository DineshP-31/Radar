package com.dp.radar.com.dp.radar.domain.repositories

interface ILoginRepository {

    fun saveEmail(email: String)
    fun saveUserId(userId: Long)

    fun clearEmail()

    fun isLoggedIn(): Boolean
    fun getUserId(): Long
}