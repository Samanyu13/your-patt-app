package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: String,
    val categoryId: String,
    val limit: Double,
    val period: BudgetPeriod
)

@Serializable
enum class BudgetPeriod {
    WEEKLY, MONTHLY, CUSTOM
}
