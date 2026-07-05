package com.happyminds.thepattapp.presentation.groupdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.happyminds.thepattapp.domain.models.User
import kotlin.math.abs
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupAddExpenseScreen(
    viewModel: GroupDetailsViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val group by viewModel.group.collectAsState()
    val members = remember(group) {
        val list = mutableListOf(User(id = "current_user", name = "You"))
        group?.let { list.addAll(it.members) }
        list
    }

    var desc by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var payer by remember { mutableStateOf(members.first()) }
    
    var showSplitDialog by remember { mutableStateOf(false) }
    var selectedPayees by remember(members) { mutableStateOf(members.toSet()) }
    var customAllocations by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var isCustomSplit by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("What was this for?") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            HorizontalDivider()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Paid by:", style = MaterialTheme.typography.titleSmall)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = payer.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member.name) },
                                onClick = {
                                    payer = member
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("For:", style = MaterialTheme.typography.titleSmall)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSplitDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null)
                        val displayPeople = if (selectedPayees.size == members.size) "Everyone" else "${selectedPayees.size} people"
                        val displayType = if (!isCustomSplit) "split equally" else "custom split"
                        Text(
                            text = "For $displayPeople, $displayType",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    val finalAllocations = if (!isCustomSplit) {
                        val share = amt / (selectedPayees.size.takeIf { it > 0 } ?: 1)
                        selectedPayees.associate { it.id to share }
                    } else {
                        customAllocations
                    }

                    viewModel.addAdvancedExpense(
                        description = desc,
                        amount = amt,
                        payerId = payer.id,
                        payeeIds = selectedPayees.map { it.id },
                        customSplit = if (!isCustomSplit) null else finalAllocations
                    )
                    onSuccess()
                },
                enabled = desc.isNotBlank() && amountStr.toDoubleOrNull() != null && selectedPayees.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Expense")
            }
        }

        if (showSplitDialog) {
            SplitSelectionDialog(
                totalAmount = amountStr.toDoubleOrNull() ?: 0.0,
                allMembers = members,
                initialSelected = selectedPayees,
                initialCustomAllocations = if (isCustomSplit) customAllocations else null,
                onDismiss = { showSplitDialog = false },
                onConfirm = { payees, allocations ->
                    selectedPayees = payees
                    customAllocations = allocations
                    
                    // Determine if it's still an equal split
                    val amtValues = allocations.values.toList()
                    val firstAmt = amtValues.firstOrNull() ?: 0.0
                    isCustomSplit = if (amtValues.isEmpty()) false 
                                    else amtValues.any { abs(it - firstAmt) > 0.01 }
                    
                    showSplitDialog = false
                }
            )
        }
    }
}

@Composable
fun SplitSelectionDialog(
    totalAmount: Double,
    allMembers: List<User>,
    initialSelected: Set<User>,
    initialCustomAllocations: Map<String, Double>?,
    onDismiss: () -> Unit,
    onConfirm: (Set<User>, Map<String, Double>) -> Unit
) {
    var selectedPayees by remember { mutableStateOf(initialSelected) }
    
    var amounts by remember(totalAmount, selectedPayees) {
        val currentMap = mutableMapOf<String, Double>()
        if (initialCustomAllocations != null && initialCustomAllocations.keys.intersect(selectedPayees.map { it.id }.toSet()).isNotEmpty()) {
            currentMap.putAll(initialCustomAllocations)
        } else {
            val share = if (selectedPayees.isNotEmpty()) totalAmount / selectedPayees.size else 0.0
            selectedPayees.forEach { currentMap[it.id] = share }
        }
        mutableStateOf(currentMap.toMap())
    }

    fun updateSplitCascading(changedUserId: String, newValue: Double) {
        val otherSelectedIds = selectedPayees.map { it.id }.filter { it != changedUserId }
        if (otherSelectedIds.isEmpty()) {
            amounts = mapOf(changedUserId to totalAmount)
            return
        }

        val remainingTotal = (totalAmount - newValue).coerceAtLeast(0.0)
        val shareForOthers = remainingTotal / otherSelectedIds.size
        
        val newMap = amounts.toMutableMap()
        newMap[changedUserId] = newValue
        otherSelectedIds.forEach { newMap[it] = shareForOthers }
        amounts = newMap.toMap()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Split with:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(allMembers.filter { selectedPayees.contains(it) }) { member ->
                        val currentVal = amounts[member.id] ?: 0.0
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(member.name, modifier = Modifier.weight(1f))

                            val percentage = if (totalAmount > 0) {
                                (round((currentVal / totalAmount * 100) * 100) / 100.0).toString()
                            } else "0"
                            
                            OutlinedTextField(
                                value = percentage,
                                onValueChange = { newVal ->
                                    val p = newVal.toDoubleOrNull() ?: 0.0
                                    val a = (p / 100.0) * totalAmount
                                    updateSplitCascading(member.id, a)
                                },
                                modifier = Modifier.width(85.dp),
                                suffix = { Text("%") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                textStyle = MaterialTheme.typography.bodySmall,
                                singleLine = true
                            )

                            val amountDisplay = (round(currentVal * 100) / 100.0).toString()
                            OutlinedTextField(
                                value = amountDisplay,
                                onValueChange = { newVal ->
                                    val a = newVal.toDoubleOrNull() ?: 0.0
                                    updateSplitCascading(member.id, a)
                                },
                                modifier = Modifier.width(105.dp),
                                prefix = { Text("₹") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                textStyle = MaterialTheme.typography.bodySmall,
                                singleLine = true
                            )
                        }
                    }
                }
                
                val currentTotal = amounts.values.sum()
                val diff = totalAmount - currentTotal
                val isMatch = abs(diff) < 0.01
                Surface(
                    color = if (isMatch) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
                        val statusText = if (isMatch) "Sum matches total" 
                                        else "Diff: ${ (round(diff * 100) / 100.0) }"
                        Text(text = statusText, style = MaterialTheme.typography.labelSmall)
                    }
                }

                HorizontalDivider()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allMembers) { member ->
                            PayeeCircleItem(
                                user = member,
                                isSelected = selectedPayees.contains(member),
                                onClick = {
                                    val newSelected = if (selectedPayees.contains(member)) {
                                        selectedPayees - member
                                    } else {
                                        selectedPayees + member
                                    }
                                    selectedPayees = newSelected
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(selectedPayees, amounts) },
                        modifier = Modifier.weight(1f),
                        enabled = selectedPayees.isNotEmpty()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
fun PayeeCircleItem(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val initial = if (user.name.isNotEmpty()) user.name.take(1).uppercase() else "?"
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(2.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Text(
            text = user.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
