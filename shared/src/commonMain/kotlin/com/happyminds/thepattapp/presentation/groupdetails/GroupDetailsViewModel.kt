package com.happyminds.thepattapp.presentation.groupdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Group
import com.happyminds.thepattapp.domain.models.Settlement
import com.happyminds.thepattapp.domain.models.User
import com.happyminds.thepattapp.domain.repository.ExpenseRepository
import com.happyminds.thepattapp.domain.services.DebtSimplifier
import com.happyminds.thepattapp.domain.services.OcrService
import com.happyminds.thepattapp.domain.services.SplitCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class GroupDetailsViewModel(
    private val groupId: String?, // null for Miscellaneous
    private val repository: ExpenseRepository,
    private val debtSimplifier: DebtSimplifier,
    private val splitCalculator: SplitCalculator,
    private val ocrService: OcrService
) : ViewModel() {

    val group: StateFlow<Group?> = if (groupId != null && groupId.isNotBlank()) {
        repository.getGroup(groupId)
    } else {
        flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val expenses: StateFlow<List<Expense>> = repository.getExpenses(groupId?.takeIf { it.isNotBlank() })
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFriends: StateFlow<List<User>> = repository.getUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settlements: StateFlow<List<Settlement>> = combine(expenses, group) { expenses, group ->
        val currency = group?.baseCurrency ?: "USD"
        debtSimplifier.simplify(expenses, currency)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMiscellaneous: Boolean = groupId == null || groupId.isBlank()

    fun addExpense(description: String, amount: Double, splitType: com.happyminds.thepattapp.domain.models.SplitType = com.happyminds.thepattapp.domain.models.SplitType.EQUAL) {
        viewModelScope.launch {
            val currentGroup = group.value
            val memberIds = currentGroup?.members?.map { it.id }?.ifEmpty { listOf("current_user") } ?: listOf("current_user")
            val splitAllocations = splitCalculator.calculateSplit(amount, memberIds, splitType)
            
            val expense = Expense(
                id = Random.nextInt().toString(),
                groupId = groupId?.takeIf { it.isNotBlank() },
                description = description,
                amount = amount,
                currency = currentGroup?.baseCurrency ?: "USD",
                timestamp = 0,
                payerAllocations = mapOf("current_user" to amount),
                splitAllocations = splitAllocations,
                splitType = splitType
            )
            repository.upsertExpense(expense)
        }
    }

    fun addMember(name: String) {
        val currentGroup = group.value ?: return
        viewModelScope.launch {
            val newUser = User(id = Random.nextInt().toString(), name = name, isPlaceholder = true)
            repository.upsertUser(newUser)
            val updatedGroup = currentGroup.copy(members = currentGroup.members + newUser)
            repository.upsertGroup(updatedGroup)
        }
    }

    fun addExistingMember(user: User) {
        val currentGroup = group.value ?: return
        if (currentGroup.members.any { it.id == user.id }) return
        viewModelScope.launch {
            val updatedGroup = currentGroup.copy(members = currentGroup.members + user)
            repository.upsertGroup(updatedGroup)
        }
    }

    fun settleGroup() {
        val currentGroup = group.value ?: return
        viewModelScope.launch {
            repository.upsertGroup(currentGroup.copy(isSettled = true))
        }
    }

    fun scanReceipt(imageData: ByteArray, onResult: (String, Double) -> Unit) {
        viewModelScope.launch {
            val result = ocrService.scanReceipt(imageData)
            val description = result.items.joinToString { it.description }
            onResult(description, result.totalAmount)
        }
    }
}
