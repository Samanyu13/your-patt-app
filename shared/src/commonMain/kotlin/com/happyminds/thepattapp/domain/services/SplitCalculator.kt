package com.happyminds.thepattapp.domain.services

import com.happyminds.thepattapp.domain.models.SplitType

class SplitCalculator {

    /**
     * Calculates liabilities for each user based on the split type and inputs.
     * 
     * @param totalAmount The total amount of the expense.
     * @param memberIds The list of users involved in the split.
     * @param splitType The method of splitting.
     * @param inputs Optional custom inputs for types like PERCENTAGE, SHARES, or ITEMIZED.
     *               For PERCENTAGE: UserID -> Percentage (0.0 to 100.0)
     *               For SHARES: UserID -> Number of shares
     *               For ITEMIZED: UserID -> Sum of item costs
     * @return UserID -> Calculated liability amount
     */
    fun calculateSplit(
        totalAmount: Double,
        memberIds: List<String>,
        splitType: SplitType,
        inputs: Map<String, Double> = emptyMap()
    ): Map<String, Double> {
        if (memberIds.isEmpty()) return emptyMap()

        return when (splitType) {
            SplitType.EQUAL -> {
                val perPerson = totalAmount / memberIds.size
                memberIds.associateWith { perPerson }
            }
            SplitType.PERCENTAGE -> {
                inputs.mapValues { it.value / 100.0 * totalAmount }
            }
            SplitType.SHARES -> {
                val totalShares = inputs.values.sum()
                if (totalShares == 0.0) return emptyMap()
                val perShare = totalAmount / totalShares
                inputs.mapValues { it.value * perShare }
            }
            SplitType.EXACT -> {
                inputs // Inputs are already exact amounts
            }
            SplitType.ITEMIZED -> {
                inputs // Itemized is essentially exact per person
            }
        }
    }
}
