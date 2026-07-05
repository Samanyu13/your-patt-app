package com.happyminds.thepattapp.domain.services

import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Group
import com.happyminds.thepattapp.domain.models.User
import com.happyminds.thepattapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first

class AccountEscalator(
    private val repository: ExpenseRepository
) {
    /**
     * Merges a placeholder user into a real authenticated user.
     * All groups and expenses referencing the placeholder will be updated to the real user.
     */
    suspend fun escalate(placeholderId: String, realUser: User) {
        // 1. Update all groups where the placeholder is a member
        val groups = repository.getGroups().first()
        for (group in groups) {
            if (group.members.any { it.id == placeholderId }) {
                val updatedMembers = group.members.map { 
                    if (it.id == placeholderId) realUser else it
                }
                repository.upsertGroup(group.copy(members = updatedMembers))
            }
        }

        // 2. Update all expenses where the placeholder is a payer or part of a split
        // Note: In a real implementation, we would query the DB for these specifically.
        // For the mock/simple version, we iterate groups.
        for (group in groups) {
            val expenses = repository.getExpenses(group.id).first()
            for (expense in expenses) {
                var needsUpdate = false
                val updatedPayerAllocations = expense.payerAllocations.toMutableMap()
                if (updatedPayerAllocations.containsKey(placeholderId)) {
                    val amount = updatedPayerAllocations.remove(placeholderId)!!
                    updatedPayerAllocations[realUser.id] = (updatedPayerAllocations[realUser.id] ?: 0.0) + amount
                    needsUpdate = true
                }

                val updatedSplitAllocations = expense.splitAllocations.toMutableMap()
                if (updatedSplitAllocations.containsKey(placeholderId)) {
                    val amount = updatedSplitAllocations.remove(placeholderId)!!
                    updatedSplitAllocations[realUser.id] = (updatedSplitAllocations[realUser.id] ?: 0.0) + amount
                    needsUpdate = true
                }

                if (needsUpdate) {
                    repository.upsertExpense(expense.copy(
                        payerAllocations = updatedPayerAllocations,
                        splitAllocations = updatedSplitAllocations
                    ))
                }
            }
        }
        
        // 3. Upsert the real user
        repository.upsertUser(realUser)
    }
}
