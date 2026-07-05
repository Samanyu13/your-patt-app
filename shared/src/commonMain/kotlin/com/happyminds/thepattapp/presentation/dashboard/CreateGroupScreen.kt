package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Group") },
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency (e.g. USD, EUR)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    viewModel.createGroup(name, currency)
                    onCreated()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Group")
            }
        }
    }
}
