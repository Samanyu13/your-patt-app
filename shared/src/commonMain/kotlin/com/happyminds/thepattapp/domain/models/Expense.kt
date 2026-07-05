package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: String,
    val groupId: String? = null, // null means Miscellaneous (groupless)
    val description: String,
    val amount: Double,
    val currency: String,
    val timestamp: Long,
    val payerAllocations: Map<String, Double>, // UserID -> Amount paid
    val splitAllocations: Map<String, Double>, // UserID -> Liability amount
    val splitType: SplitType = SplitType.EQUAL
)

@Serializable
enum class SplitType {
    EQUAL, EXACT, PERCENTAGE, SHARES, ITEMIZED
}
