package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

@Serializable
data class LedgerTransaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val sourceAccountId: String,
    val destinationAccountId: String? = null,
    val categoryId: String? = null,
    val timestamp: Long,
    val memo: String? = null,
    val linkedExpenseId: String? = null // Link to Split App's Expense
)
