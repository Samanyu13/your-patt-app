package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel,
    onGroupClick: (Group) -> Unit,
    onSeeAllSplit: () -> Unit,
    onSeeAllLedger: () -> Unit,
    onOpenDevTools: () -> Unit
) {
    val activeGroups by viewModel.activeGroups.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val netWorth by viewModel.netWorth.collectAsState()
    val ledgerTransactions by viewModel.transactions.collectAsState()
    val foodProgress by viewModel.foodBudgetProgress.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDevModeUnlocked by viewModel.isDevModeUnlocked.collectAsState()

    val scope = rememberCoroutineScope()
    var tapCount by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddAccountDialog by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, ${currentUser?.name ?: "User"}!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Here's your financial overview",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    
                    // Profile Icon with Dev Tools Logic
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                if (isDevModeUnlocked) {
                                    onOpenDevTools()
                                    return@clickable
                                }

                                tapCount++
                                if (tapCount in 3..6) {
                                    scope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar(
                                            "You are now ${7 - tapCount} steps away from being a developer"
                                        )
                                    }
                                } else if (tapCount >= 7) {
                                    viewModel.unlockDevMode()
                                    scope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar("Developer mode enabled!")
                                    }
                                    onOpenDevTools()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
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
                AccountCarousel(
                    accounts = accounts,
                    onAddAccount = { showAddAccountDialog = true },
                    onDeleteAccount = { accountToDelete = it }
                )
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
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )

        if (showAddAccountDialog) {
            AddAccountDialog(
                onDismiss = { showAddAccountDialog = false },
                onConfirm = { name, type, balance ->
                    viewModel.createAccount(name, type, balance)
                    showAddAccountDialog = false
                }
            )
        }

        accountToDelete?.let { account ->
            AlertDialog(
                onDismissRequest = { accountToDelete = null },
                title = { Text("Delete Account") },
                text = { Text("Are you sure you want to delete '${account.name}'? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteAccount(account.id)
                            accountToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { accountToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
