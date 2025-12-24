package com.dp.radar.com.dp.radar.data.datasources.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val name: String,
    val catchPhrase: String,
    val bs: String
)