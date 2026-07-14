package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class AccountType {
    CASH, CHECKING, SAVINGS, CREDIT
}

@Serializable
data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: String
)
