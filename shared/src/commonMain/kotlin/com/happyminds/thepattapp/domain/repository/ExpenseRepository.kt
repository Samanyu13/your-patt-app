package com.happyminds.thepattapp.domain.repository

import com.happyminds.thepattapp.domain.models.*
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    // Groups (Split App)
    fun getGroups(includeSettled: Boolean = false): Flow<List<Group>>
    fun getGroup(id: String): Flow<Group?>
    suspend fun upsertGroup(group: Group)
    suspend fun deleteGroup(id: String)

    // Expenses (Split App)
    fun getExpenses(groupId: String?): Flow<List<Expense>> // null for Miscellaneous
    suspend fun upsertExpense(expense: Expense)
    suspend fun deleteExpense(id: String)

    // Users
    fun getUsers(): Flow<List<User>>
    suspend fun upsertUser(user: User)

    // Ledger (Expense Manager)
    fun getAccounts(): Flow<List<Account>>
    fun getAccount(id: String): Flow<Account?>
    suspend fun upsertAccount(account: Account)
    
    fun getTransactions(): Flow<List<LedgerTransaction>>
    fun getTransactionsByAccount(accountId: String): Flow<List<LedgerTransaction>>
    suspend fun upsertTransaction(transaction: LedgerTransaction)
    
    fun getCategories(): Flow<List<Category>>
    suspend fun upsertCategory(category: Category)
    
    fun getBudgets(): Flow<List<Budget>>
    suspend fun upsertBudget(budget: Budget)
}
