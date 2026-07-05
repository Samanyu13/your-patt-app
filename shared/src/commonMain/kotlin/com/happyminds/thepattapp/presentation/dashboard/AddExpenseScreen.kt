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
    
    var desc by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var expanded by remember { mutableStateOf(false) }

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
                modifier = Modifier.fillMaxWidth()
            )
            
            Text("Add to:", style = MaterialTheme.typography.labelMedium)
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedGroup?.name ?: "Miscellaneous (None)",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Miscellaneous (None)") },
                        onClick = {
                            selectedGroup = null
                            expanded = false
                        }
                    )
                    HorizontalDivider()
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroup = group
                                expanded = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("+ Create New Group") },
                        onClick = {
                            expanded = false
                            onCreateGroup()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (selectedGroup == null) {
                        viewModel.addMiscExpense(desc, amt, "USD")
                    } else {
                        // For simplicity in this flow, we could add a method to VM to add to specific group
                        // or just use repository directly if we had access. 
                        // Let's assume we add a method to VM.
                        viewModel.addExpenseToGroup(selectedGroup!!.id, desc, amt)
                    }
                    onSuccess()
                },
                enabled = desc.isNotBlank() && amount.toDoubleOrNull() != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Expense")
            }
        }
    }
}
