package com.happyminds.thepattapp.domain.services

import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Settlement
import kotlin.math.abs
import kotlin.math.min

class DebtSimplifier {

    /**
     * Simplifies debts within a group to minimize the total number of transactions.
     * 
     * @param expenses The list of expenses in the group.
     * @param currency The currency to simplify (usually the group's base currency).
     * @return A list of suggested settlements.
     */
    fun simplify(expenses: List<Expense>, currency: String): List<Settlement> {
        val balances = mutableMapOf<String, Double>()

        // 1. Calculate net balance for each user
        // Balance = (Total Paid by User) - (Total Liability of User)
        for (expense in expenses) {
            if (expense.currency != currency) continue // Skip foreign currency for now or handle conversion

            expense.payerAllocations.forEach { (userId, amount) ->
                balances[userId] = (balances[userId] ?: 0.0) + amount
            }
            expense.splitAllocations.forEach { (userId, amount) ->
                balances[userId] = (balances[userId] ?: 0.0) - amount
            }
        }

        // 2. Separate debtors and creditors
        val debtors = mutableListOf<Pair<String, Double>>()
        val creditors = mutableListOf<Pair<String, Double>>()

        balances.forEach { (userId, balance) ->
            // Use a small epsilon to handle floating point precision issues
            if (balance < -0.01) {
                debtors.add(userId to abs(balance))
            } else if (balance > 0.01) {
                creditors.add(userId to balance)
            }
        }

        // Sort both by amount descending to greedy-match largest amounts first
        debtors.sortByDescending { it.second }
        creditors.sortByDescending { it.second }

        val settlements = mutableListOf<Settlement>()
        var dIdx = 0
        var cIdx = 0

        // 3. Greedy algorithm to match debtors and creditors
        val currentDebtors = debtors.map { it.first to it.second }.toMutableList()
        val currentCreditors = creditors.map { it.first to it.second }.toMutableList()

        while (dIdx < currentDebtors.size && cIdx < currentCreditors.size) {
            val debtor = currentDebtors[dIdx]
            val creditor = currentCreditors[cIdx]

            val amountToSettle = min(debtor.second, creditor.second)

            if (amountToSettle > 0.01) {
                settlements.add(
                    Settlement(
                        fromUserId = debtor.first,
                        toUserId = creditor.first,
                        amount = amountToSettle,
                        currency = currency
                    )
                )
            }

            // Update remaining amounts
            currentDebtors[dIdx] = debtor.first to (debtor.second - amountToSettle)
            currentCreditors[cIdx] = creditor.first to (creditor.second - amountToSettle)

            if (currentDebtors[dIdx].second < 0.01) dIdx++
            if (currentCreditors[cIdx].second < 0.01) cIdx++
        }

        return settlements
    }
}
