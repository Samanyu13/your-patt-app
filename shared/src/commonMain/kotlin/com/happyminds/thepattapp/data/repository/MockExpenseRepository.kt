package com.happyminds.thepattapp.data.repository

import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Group
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

    override fun getGroups(includeSettled: Boolean): Flow<List<Group>> = groups.map { 
        it.values.filter { g -> includeSettled || !g.isSettled }
    }

    override fun getGroup(id: String): Flow<Group?> = groups.map { it[id] }

    override suspend fun upsertGroup(group: Group) {
        groups.update { it + (group.id to group) }
    }

    override suspend fun deleteGroup(id: String) {
        groups.update { it - id }
    }

    override fun getExpenses(groupId: String?): Flow<List<Expense>> =
        expenses.map { it.values.filter { e -> e.groupId == groupId } }

    override suspend fun upsertExpense(expense: Expense) {
        expenses.update { it + (expense.id to expense) }
    }

    override suspend fun deleteExpense(id: String) {
        expenses.update { it - id }
    }

    override fun getUsers(): Flow<List<User>> = users.map { it.values.toList() }

    override suspend fun upsertUser(user: User) {
        users.update { it + (user.id to user) }
    }
}
