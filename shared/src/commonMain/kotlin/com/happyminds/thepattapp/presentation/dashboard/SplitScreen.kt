package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.Group

@Composable
fun SplitScreen(
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MiscBar(
                    expenseCount = miscExpenses.size,
                    onClick = onMiscClick
                )
            }

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
            
            item { Spacer(Modifier.height(80.dp)) }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            SmallFloatingActionButton(
                onClick = onCreateGroupClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.GroupAdd, contentDescription = "Create Group")
            }
            FloatingActionButton(
                onClick = onCreateExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Receipt, contentDescription = "Add Expense")
            }
        }
    }
}
