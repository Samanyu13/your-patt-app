package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.clickable
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
import com.happyminds.thepattapp.domain.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onGroupClick: (Group) -> Unit,
    onMiscClick: () -> Unit,
    onShowSettledClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onCreateExpenseClick: () -> Unit
) {
    val activeGroups by viewModel.activeGroups.collectAsState()
    val settledGroups by viewModel.settledGroups.collectAsState()
    val miscExpenses by viewModel.miscExpenses.collectAsState()
    
    var showFabMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ThePattApp") })
        },
        floatingActionButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (showFabMenu) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 80.dp)
                    ) {
                        SmallFloatingActionButton(
                            onClick = { 
                                showFabMenu = false
                                onCreateGroupClick()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = "Create Group")
                        }
                        SmallFloatingActionButton(
                            onClick = { 
                                showFabMenu = false
                                onCreateExpenseClick()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = "Create Expense")
                        }
                    }
                }
                FloatingActionButton(onClick = { showFabMenu = !showFabMenu }) {
                    Icon(if (showFabMenu) Icons.Default.Close else Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Misc Transactions Bar
            item {
                MiscBar(
                    expenseCount = miscExpenses.size,
                    onClick = onMiscClick
                )
            }

            // Settled Groups Bar
            item {
                SettledBar(
                    groupCount = settledGroups.size,
                    onClick = onShowSettledClick
                )
            }

            item {
                Text("Groups", style = MaterialTheme.typography.titleMedium)
            }

            if (activeGroups.isEmpty()) {
                item {
                    Text("No groups yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                items(activeGroups) { group ->
                    GroupItem(group, onClick = { onGroupClick(group) })
                }
            }
        }
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
