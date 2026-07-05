package com.happyminds.thepattapp.presentation.groupdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.Expense
import com.happyminds.thepattapp.domain.models.Settlement
import com.happyminds.thepattapp.domain.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    viewModel: GroupDetailsViewModel,
    onBackClick: () -> Unit,
    onShareInvite: (String) -> Unit
) {
    val group by viewModel.group.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val settlements by viewModel.settlements.collectAsState()
    val allFriends by viewModel.allFriends.collectAsState()
    val isMisc = viewModel.isMiscellaneous
    
    var showAddExpense by remember { mutableStateOf(false) }
    var showAddMember by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPersonalExpensePrompt by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(if (isMisc) "Miscellaneous" else (group?.name ?: "Loading..."))
                        if (!isMisc && group?.members?.isEmpty() == true) {
                            Text("Only you are here", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (!isMisc) {
                        IconButton(onClick = { showAddMember = true }) { Icon(Icons.Default.PersonAdd, contentDescription = "Add Member") }
                        IconButton(onClick = { 
                            group?.let { onShareInvite("https://thepattapp.com/join?token=${it.id}") } 
                        }) { Icon(Icons.Default.Share, contentDescription = "Invite") }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                if (!isMisc && group?.members?.isEmpty() == true) {
                    showPersonalExpensePrompt = true
                } else {
                    showAddExpense = true 
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!isMisc) {
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Expenses") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Balances") })
                }
            }
            
            if (isMisc || selectedTab == 0) {
                ExpenseList(expenses)
            } else {
                BalanceList(settlements)
            }
        }
        
        if (showAddMember) {
            AddMemberDialog(
                friends = allFriends,
                onDismiss = { showAddMember = false },
                onAddManual = { name ->
                    viewModel.addMember(name)
                    showAddMember = false
                },
                onAddExisting = { user ->
                    viewModel.addExistingMember(user)
                    showAddMember = false
                }
            )
        }

        if (showAddExpense) {
            AddExpenseDialog(
                onDismiss = { showAddExpense = false },
                onAdd = { desc, amount ->
                    viewModel.addExpense(desc, amount)
                    showAddExpense = false
                }
            )
        }

        if (showPersonalExpensePrompt) {
            AlertDialog(
                onDismissRequest = { showPersonalExpensePrompt = false },
                title = { Text("Add Personal Expense?") },
                text = { Text("You are the only member in this group. This will be saved as your personal expense.") },
                confirmButton = {
                    Button(onClick = { 
                        showPersonalExpensePrompt = false
                        showAddExpense = true 
                    }) { Text("Proceed") }
                },
                dismissButton = {
                    TextButton(onClick = { showPersonalExpensePrompt = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun ExpenseList(expenses: List<Expense>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (expenses.isEmpty()) {
            item { Text("No expenses yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline) }
        }
        items(expenses) { expense ->
            ExpenseItem(expense)
        }
    }
}

@Composable
fun BalanceList(settlements: List<Settlement>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (settlements.isEmpty()) {
            item { Text("Everything is settled!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline) }
        }
        items(settlements) { settlement ->
            SettlementItem(settlement)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(expense.description, style = MaterialTheme.typography.titleMedium)
                Text("Paid by you", style = MaterialTheme.typography.labelSmall)
            }
            Text("${expense.amount} ${expense.currency}", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun SettlementItem(settlement: Settlement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            "${settlement.fromUserId} owes ${settlement.toUserId} ${settlement.amount} ${settlement.currency}",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun AddMemberDialog(
    friends: List<User>,
    onDismiss: () -> Unit,
    onAddManual: (String) -> Unit,
    onAddExisting: (User) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Member") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (friends.isNotEmpty()) {
                    Text("Choose from existing friends:", style = MaterialTheme.typography.labelMedium)
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(friends) { friend ->
                            ListItem(
                                headlineContent = { Text(friend.name) },
                                modifier = Modifier.clickable { onAddExisting(friend) }
                            )
                        }
                    }
                    HorizontalDivider()
                }
                Text("Add new person:", style = MaterialTheme.typography.labelMedium)
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            }
        },
        confirmButton = {
            Button(onClick = { onAddManual(name) }, enabled = name.isNotBlank()) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                TextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(desc, amount.toDoubleOrNull() ?: 0.0) }, enabled = desc.isNotBlank() && amount.toDoubleOrNull() != null) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
