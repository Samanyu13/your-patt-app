package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.happyminds.thepattapp.domain.models.*

@Composable
fun NetWorthCard(netWorth: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Total Net Worth", style = MaterialTheme.typography.labelLarge)
            Text(
                "₹${netWorth.toString()}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AccountCarousel(accounts: List<Account>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(accounts) { account ->
            AccountCard(account)
        }
    }
}

@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier.width(160.dp).height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (account.type == AccountType.CREDIT) 
                MaterialTheme.colorScheme.errorContainer 
            else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(account.name, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            Text(
                "₹${account.balance}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                account.type.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun BudgetProgressCard(category: String, spentPercentage: Double, timePercentage: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("${(spentPercentage * 100).toInt()}% spent", style = MaterialTheme.typography.labelSmall)
            }
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(MaterialTheme.shapes.extraSmall).background(MaterialTheme.colorScheme.surfaceVariant)) {
                // Time progress (ambient blue)
                Box(modifier = Modifier.fillMaxWidth(timePercentage.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
                
                // Spent progress
                val barColor = if (spentPercentage > timePercentage) Color(0xFFFFA000) else MaterialTheme.colorScheme.primary
                Box(modifier = Modifier.fillMaxWidth(spentPercentage.toFloat()).fillMaxHeight().background(barColor))
            }
            Text(
                if (spentPercentage > timePercentage) "You're spending faster than usual!" else "On track for the month",
                style = MaterialTheme.typography.labelSmall,
                color = if (spentPercentage > timePercentage) Color(0xFFFFA000) else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun LedgerTransactionItem(transaction: LedgerTransaction) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(transaction.memo ?: "Transaction", style = MaterialTheme.typography.bodyLarge)
            val categoryDisplay = transaction.categoryId?.replace("_", " -> ") ?: "Uncategorized"
            Text(
                categoryDisplay,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}₹${transaction.amount}",
            style = MaterialTheme.typography.titleMedium,
            color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MiscBar(expenseCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null)
                Spacer(Modifier.width(16.dp))
                Text("Miscellaneous Transactions", style = MaterialTheme.typography.titleMedium)
            }
            if (expenseCount > 0) {
                Badge { Text("$expenseCount") }
            }
        }
    }
}

@Composable
fun SettledBar(groupCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(16.dp))
                Text("Settled Groups", style = MaterialTheme.typography.titleMedium)
            }
            if (groupCount > 0) {
                Badge { Text("$groupCount") }
            }
        }
    }
}

@Composable
fun GroupItem(group: Group, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(group.name, style = MaterialTheme.typography.titleLarge)
            Text("Click to view details", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}
