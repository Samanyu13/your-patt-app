package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.happyminds.thepattapp.domain.models.*

@Composable
fun OwnExpenseScreen(
    viewModel: DashboardViewModel,
    onAddExpense: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    val ledgerTransactions by viewModel.transactions.collectAsState()
    val foodProgress by viewModel.foodBudgetProgress.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Your Accounts", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                AccountCarousel(accounts)
            }

            item {
                Text("Monthly Budget", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                BudgetProgressCard("Food & Drinks", foodProgress, 0.45)
            }

            item {
                Text("Transaction History", style = MaterialTheme.typography.titleMedium)
            }

            if (ledgerTransactions.isEmpty()) {
                item {
                    Text("No transactions yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                items(ledgerTransactions.sortedByDescending { it.timestamp }) { trans ->
                    LedgerTransactionItem(trans)
                }
            }
            
            item { Spacer(Modifier.height(80.dp)) }
        }

        FloatingActionButton(
            onClick = onAddExpense,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Personal Expense")
        }
    }
}
