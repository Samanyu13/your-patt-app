package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    val userId: String,
    val amount: Double,
    val currency: String
)

@Serializable
data class Settlement(
    val fromUserId: String,
    val toUserId: String,
    val amount: Double,
    val currency: String
)
