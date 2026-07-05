package com.happyminds.thepattapp.domain.repository

import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Group
import com.happyminds.thepattapp.domain.models.User
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    // Groups
    fun getGroups(includeSettled: Boolean = false): Flow<List<Group>>
    fun getGroup(id: String): Flow<Group?>
    suspend fun upsertGroup(group: Group)
    suspend fun deleteGroup(id: String)

    // Expenses
    fun getExpenses(groupId: String?): Flow<List<Expense>> // null for Miscellaneous
    suspend fun upsertExpense(expense: Expense)
    suspend fun deleteExpense(id: String)

    // Users
    fun getUsers(): Flow<List<User>>
    suspend fun upsertUser(user: User)
}
