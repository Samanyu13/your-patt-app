package com.happyminds.thepattapp.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.happyminds.thepattapp.domain.models.*

import com.happyminds.thepattapp.ui.components.PattTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLedgerTransactionScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    val allCategories by viewModel.categories.collectAsState()
    
    var amount by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var selectedAccount by remember(accounts) { mutableStateOf(accounts.firstOrNull()) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    
    val parentCategories = remember(allCategories) { allCategories.filter { it.parentCategoryId == null } }
    var selectedParentCategory by remember(parentCategories) { mutableStateOf(parentCategories.find { it.id == "uncategorized" } ?: parentCategories.firstOrNull()) }
    
    val subCategories = remember(allCategories, selectedParentCategory) { 
        allCategories.filter { it.parentCategoryId == selectedParentCategory?.id } 
    }
    var selectedSubCategory by remember(subCategories) { mutableStateOf<Category?>(null) }
    
    var accountExpanded by remember { mutableStateOf(false) }
    var parentCatExpanded by remember { mutableStateOf(false) }
    var subCatExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Ledger Entry") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ... (rest of the content)
            // Transaction Type Segmented Button
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = transactionType == TransactionType.EXPENSE,
                    onClick = { transactionType = TransactionType.EXPENSE },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Expense")
                }
                SegmentedButton(
                    selected = transactionType == TransactionType.INCOME,
                    onClick = { transactionType = TransactionType.INCOME },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Income")
                }
            }

            PattTextField(
                value = amount,
                onValueChange = { amount = it },
                label = "Amount",
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            PattTextField(
                value = memo,
                onValueChange = { memo = it },
                label = "What was this for? (Description)",
                modifier = Modifier.fillMaxWidth()
            )

            Text("Section (Category)", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(
                expanded = parentCatExpanded,
                onExpandedChange = { parentCatExpanded = !parentCatExpanded }
            ) {
                PattTextField(
                    value = selectedParentCategory?.name ?: "Select Section",
                    onValueChange = {},
                    label = "Section",
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentCatExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = parentCatExpanded,
                    onDismissRequest = { parentCatExpanded = false }
                ) {
                    parentCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedParentCategory = cat
                                selectedSubCategory = null
                                parentCatExpanded = false
                            }
                        )
                    }
                }
            }

            if (subCategories.isNotEmpty()) {
                Text("Subsection (Subcategory)", style = MaterialTheme.typography.labelMedium)
                ExposedDropdownMenuBox(
                    expanded = subCatExpanded,
                    onExpandedChange = { subCatExpanded = !subCatExpanded }
                ) {
                    PattTextField(
                        value = selectedSubCategory?.name ?: "Select Subsection (Optional)",
                        onValueChange = {},
                        label = "Subsection",
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCatExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = subCatExpanded,
                        onDismissRequest = { subCatExpanded = false }
                    ) {
                        subCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedSubCategory = cat
                                    subCatExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text("Source Account", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(
                expanded = accountExpanded,
                onExpandedChange = { accountExpanded = !accountExpanded }
            ) {
                PattTextField(
                    value = selectedAccount?.name ?: "Select Account",
                    onValueChange = {},
                    label = "Account",
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    val accId = selectedAccount?.id ?: return@Button
                    val catId = selectedSubCategory?.id ?: selectedParentCategory?.id ?: "uncategorized"
                    viewModel.addLedgerEntry(
                        amount = amt,
                        memo = memo,
                        accountId = accId,
                        type = transactionType,
                        categoryId = catId
                    )
                    onSuccess()
                },
                enabled = amount.toDoubleOrNull() != null && memo.isNotBlank() && selectedAccount != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Ledger")
            }
        }
    }
}
