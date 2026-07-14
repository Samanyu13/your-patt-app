package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onCreateGroup: () -> Unit,
    onSuccess: () -> Unit
) {
    val groups by viewModel.activeGroups.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    
    var desc by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var selectedAccount by remember(accounts) { mutableStateOf(accounts.firstOrNull()) }
    var groupExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("₹") }
            )
            
            Text("Pay from Account:", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(
                expanded = accountExpanded,
                onExpandedChange = { accountExpanded = !accountExpanded }
            ) {
                TextField(
                    value = selectedAccount?.name ?: "Select Account",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = accountExpanded,
                    onDismissRequest = { accountExpanded = false }
                ) {
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text("${account.name} (₹${account.balance})") },
                            onClick = {
                                selectedAccount = account
                                accountExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Add to Split Group:", style = MaterialTheme.typography.labelMedium)
            
            ExposedDropdownMenuBox(
                expanded = groupExpanded,
                onExpandedChange = { groupExpanded = !groupExpanded }
            ) {
                TextField(
                    value = selectedGroup?.name ?: "Miscellaneous (None)",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = groupExpanded,
                    onDismissRequest = { groupExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Miscellaneous (None)") },
                        onClick = {
                            selectedGroup = null
                            groupExpanded = false
                        }
                    )
                    HorizontalDivider()
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroup = group
                                groupExpanded = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("+ Create New Group") },
                        onClick = {
                            groupExpanded = false
                            onCreateGroup()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    val accId = selectedAccount?.id ?: "acc1"
                    if (selectedGroup == null) {
                        viewModel.addMiscExpense(desc, amt, "INR", accId)
                    } else {
                        viewModel.addExpenseToGroup(selectedGroup!!.id, desc, amt, accId)
                    }
                    onSuccess()
                },
                enabled = desc.isNotBlank() && amount.toDoubleOrNull() != null && selectedAccount != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Expense")
            }
        }
    }
}
