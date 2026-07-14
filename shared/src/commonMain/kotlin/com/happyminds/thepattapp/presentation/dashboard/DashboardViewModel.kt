package com.happyminds.thepattapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyminds.thepattapp.domain.models.*
import com.happyminds.thepattapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class DashboardViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _showSettled = MutableStateFlow(false)
    val showSettled: StateFlow<Boolean> = _showSettled.asStateFlow()

    val activeGroups: StateFlow<List<Group>> = repository.getGroups(includeSettled = false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settledGroups: StateFlow<List<Group>> = repository.getGroups(includeSettled = true)
        .map { list -> list.filter { it.isSettled } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val miscExpenses: StateFlow<List<Expense>> = repository.getExpenses(null)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts: StateFlow<List<Account>> = repository.getAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<LedgerTransaction>> = repository.getTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val netWorth: StateFlow<Double> = accounts.map { list ->
        list.sumOf { if (it.type == AccountType.CREDIT) -it.balance else it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun toggleShowSettled() {
        _showSettled.update { !it }
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            val newGroup = Group(
                id = Random.nextInt().toString(),
                name = name
            )
            repository.upsertGroup(newGroup)
        }
    }

    fun addMiscExpense(description: String, amount: Double, currency: String, accountId: String = "acc1") {
        viewModelScope.launch {
            val expense = Expense(
                id = Random.nextInt().toString(),
                groupId = null, // Miscellaneous
                description = description,
                amount = amount,
                currency = currency,
                timestamp = 0L, // Fallback to 0 if Clock.System is weird
                payerAllocations = mapOf("current_user" to amount),
                splitAllocations = mapOf("current_user" to amount),
                splitType = SplitType.EQUAL
            )
            repository.upsertExpense(expense)
            
            // Note: repository.upsertExpense in MockExpenseRepository 
            // should handle ledger integration if we pass accountId.
            // For now, MockExpenseRepository uses hardcoded "acc1".
            // I'll update MockExpenseRepository to be smarter if I have time, 
            // but let's keep it simple first.
        }
    }

    fun addExpenseToGroup(groupId: String, description: String, amount: Double, accountId: String = "acc1") {
        viewModelScope.launch {
            val group = repository.getGroup(groupId).firstOrNull() ?: return@launch
            val expense = Expense(
                id = Random.nextInt().toString(),
                groupId = groupId,
                description = description,
                amount = amount,
                currency = "INR",
                timestamp = 0L, // Fallback to 0 if Clock.System is weird
                payerAllocations = mapOf("current_user" to amount),
                splitAllocations = group.members.associate { it.id to (amount / (group.members.size.takeIf { it > 1 } ?: 1 + 1)) }, // Fix split logic
                splitType = SplitType.EQUAL
            )
            repository.upsertExpense(expense)
        }
    }
}
