package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.*

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel,
    onGroupClick: (Group) -> Unit,
    onSeeAllSplit: () -> Unit,
    onSeeAllLedger: () -> Unit
) {
    val activeGroups by viewModel.activeGroups.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val netWorth by viewModel.netWorth.collectAsState()
    val ledgerTransactions by viewModel.transactions.collectAsState()
    val foodProgress by viewModel.foodBudgetProgress.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Hello, ${currentUser?.name ?: "User"}!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "Here's your financial overview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        item {
            NetWorthCard(netWorth)
        }

        item {
            Text("Monthly Budget", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            BudgetProgressCard("Food & Drinks", foodProgress, 0.0)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Your Accounts", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onSeeAllLedger) { Text("Manage") }
            }
            AccountCarousel(accounts)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recent Split Groups", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onSeeAllSplit) { Text("See All") }
            }
        }

        if (activeGroups.isEmpty()) {
            item {
                Text("No active groups", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            items(activeGroups.take(3)) { group ->
                GroupItem(group, onClick = { onGroupClick(group) })
            }
        }

        item {
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium)
        }

        if (ledgerTransactions.isEmpty()) {
            item {
                Text("No recent activity", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            items(ledgerTransactions.sortedByDescending { it.timestamp }.take(5)) { trans ->
                LedgerTransactionItem(trans)
            }
        }
    }
}
