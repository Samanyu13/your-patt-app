package com.happyminds.thepattapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Group
import com.happyminds.thepattapp.domain.models.SplitType
import com.happyminds.thepattapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    fun toggleShowSettled() {
        _showSettled.update { !it }
    }

    fun createGroup(name: String, currency: String) {
        viewModelScope.launch {
            val newGroup = Group(
                id = Random.nextInt().toString(),
                name = name,
                baseCurrency = currency
            )
            repository.upsertGroup(newGroup)
        }
    }

    fun addMiscExpense(description: String, amount: Double, currency: String) {
        viewModelScope.launch {
            val expense = Expense(
                id = Random.nextInt().toString(),
                groupId = null, // Miscellaneous
                description = description,
                amount = amount,
                currency = currency,
                timestamp = 0,
                payerAllocations = mapOf("current_user" to amount),
                splitAllocations = mapOf("current_user" to amount),
                splitType = SplitType.EQUAL
            )
            repository.upsertExpense(expense)
        }
    }

    fun addExpenseToGroup(groupId: String, description: String, amount: Double) {
        viewModelScope.launch {
            val group = repository.getGroup(groupId).firstOrNull() ?: return@launch
            val expense = Expense(
                id = Random.nextInt().toString(),
                groupId = groupId,
                description = description,
                amount = amount,
                currency = group.baseCurrency,
                timestamp = 0,
                payerAllocations = mapOf("current_user" to amount),
                splitAllocations = group.members.associate { it.id to (amount / (group.members.size.takeIf { it > 0 } ?: 1)) },
                splitType = SplitType.EQUAL
            )
            repository.upsertExpense(expense)
        }
    }
}
