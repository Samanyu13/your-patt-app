package com.happyminds.thepattapp.data.repository

import com.happyminds.thepattapp.domain.models.Account
import com.happyminds.thepattapp.domain.models.AccountType
import com.happyminds.thepattapp.domain.models.Budget
import com.happyminds.thepattapp.domain.models.Category
import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Group
import com.happyminds.thepattapp.domain.models.LedgerTransaction
import com.happyminds.thepattapp.domain.models.TransactionType
import com.happyminds.thepattapp.domain.models.User
import com.happyminds.thepattapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class MockExpenseRepository : ExpenseRepository {
    private val groups = MutableStateFlow<Map<String, Group>>(emptyMap())
    private val expenses = MutableStateFlow<Map<String, Expense>>(emptyMap())
    private val users = MutableStateFlow<Map<String, User>>(emptyMap())

    private val accounts = MutableStateFlow<Map<String, Account>>(mapOf(
        "acc1" to Account("acc1", "Checking Account", AccountType.CHECKING, 2500.0, "INR"),
        "acc2" to Account("acc2", "Cash", AccountType.CASH, 500.0, "INR"),
        "acc3" to Account("acc3", "Savings", AccountType.SAVINGS, 10000.0, "INR")
    ))
    private val ledgerTransactions = MutableStateFlow<Map<String, LedgerTransaction>>(emptyMap())
    private val categories = MutableStateFlow<Map<String, Category>>(mapOf(
        "cat1" to Category("cat1", "Food & Dining", icon = "restaurant"),
        "cat2" to Category("cat2", "Shopping", icon = "shopping_cart"),
        "cat3" to Category("cat3", "Transportation", icon = "directions_car")
    ))
    private val budgets = MutableStateFlow<Map<String, Budget>>(emptyMap())

    override fun getGroups(includeSettled: Boolean): Flow<List<Group>> = groups.map { 
        it.values.filter { g -> includeSettled || !g.isSettled }.toList()
    }

    override fun getGroup(id: String): Flow<Group?> = groups.map { it[id] }

    override suspend fun upsertGroup(group: Group) {
        groups.update { it + (group.id to group) }
    }

    override suspend fun deleteGroup(id: String) {
        groups.update { it - id }
    }

    override fun getExpenses(groupId: String?): Flow<List<Expense>> =
        expenses.map { it.values.filter { e -> e.groupId == groupId }.toList() }

    override suspend fun upsertExpense(expense: Expense) {
        expenses.update { it + (expense.id to expense) }
        
        // Integration: If current_user paid or owes, update Ledger
        // For simplicity, if current_user paid, it's an Expense from their default account
        val amountPaidByMe = expense.payerAllocations["current_user"] ?: 0.0
        if (amountPaidByMe > 0) {
            val trans = LedgerTransaction(
                id = "ledger_${expense.id}",
                amount = amountPaidByMe,
                type = TransactionType.EXPENSE,
                sourceAccountId = "acc1", // Default to Checking for now
                categoryId = "cat1", // Default to Food
                timestamp = expense.timestamp,
                memo = "Split App: ${expense.description}",
                linkedExpenseId = expense.id
            )
            upsertTransaction(trans)
        }
    }

    override suspend fun deleteExpense(id: String) {
        expenses.update { it - id }
        ledgerTransactions.update { it - "ledger_$id" }
    }

    override fun getUsers(): Flow<List<User>> = users.map { it.values.toList() }

    override suspend fun upsertUser(user: User) {
        users.update { it + (user.id to user) }
    }

    // Ledger Methods
    override fun getAccounts(): Flow<List<Account>> = accounts.map { it.values.toList() }
    override fun getAccount(id: String): Flow<Account?> = accounts.map { it[id] }
    override suspend fun upsertAccount(account: Account) {
        accounts.update { it + (account.id to account) }
    }

    override fun getTransactions(): Flow<List<LedgerTransaction>> = ledgerTransactions.map { it.values.toList() }
    override fun getTransactionsByAccount(accountId: String): Flow<List<LedgerTransaction>> = 
        ledgerTransactions.map { it.values.filter { t -> t.sourceAccountId == accountId || t.destinationAccountId == accountId }.toList() }

    override suspend fun upsertTransaction(transaction: LedgerTransaction) {
        ledgerTransactions.update { it + (transaction.id to transaction) }
        
        // Update account balances
        when (transaction.type) {
            TransactionType.EXPENSE -> {
                val acc = accounts.value[transaction.sourceAccountId]
                if (acc != null) {
                    val newBalance = if (acc.type == AccountType.CREDIT) 
                        acc.balance + transaction.amount 
                    else 
                        acc.balance - transaction.amount
                    upsertAccount(acc.copy(balance = newBalance))
                }
            }
            TransactionType.INCOME -> {
                val acc = accounts.value[transaction.sourceAccountId]
                if (acc != null) {
                    val newBalance = if (acc.type == AccountType.CREDIT) 
                        acc.balance - transaction.amount 
                    else 
                        acc.balance + transaction.amount
                    upsertAccount(acc.copy(balance = newBalance))
                }
            }
            TransactionType.TRANSFER -> {
                val src = accounts.value[transaction.sourceAccountId]
                val dst = accounts.value[transaction.destinationAccountId ?: ""]
                if (src != null) upsertAccount(src.copy(balance = src.balance - transaction.amount))
                if (dst != null) upsertAccount(dst.copy(balance = dst.balance + transaction.amount))
            }
        }
    }

    override fun getCategories(): Flow<List<Category>> = categories.map { it.values.toList() }
    override suspend fun upsertCategory(category: Category) {
        categories.update { it + (category.id to category) }
    }

    override fun getBudgets(): Flow<List<Budget>> = budgets.map { it.values.toList() }
    override suspend fun upsertBudget(budget: Budget) {
        budgets.update { it + (budget.id to budget) }
    }
}
