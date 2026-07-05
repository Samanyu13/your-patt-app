package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettledGroupsScreen(
    viewModel: DashboardViewModel,
    onBackClick: () -> Unit,
    onGroupClick: (Group) -> Unit
) {
    val settledGroups by viewModel.settledGroups.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settled Groups") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (settledGroups.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No settled groups", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(settledGroups) { group ->
                    GroupItem(group, onClick = { onGroupClick(group) })
                }
            }
        }
    }
}
