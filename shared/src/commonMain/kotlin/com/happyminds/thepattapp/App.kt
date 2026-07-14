package com.happyminds.thepattapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happyminds.thepattapp.presentation.dashboard.*
import com.happyminds.thepattapp.presentation.groupdetails.GroupAddExpenseScreen
import com.happyminds.thepattapp.presentation.groupdetails.GroupDetailsScreen
import com.happyminds.thepattapp.presentation.groupdetails.GroupDetailsViewModel
import com.happyminds.thepattapp.ui.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

enum class MainService {
    Home, Split, OwnExpense
}

enum class Screen {
    Main,
    GroupDetails,
    MiscDetails,
    SettledGroups,
    CreateGroup,
    AddExpense,
    AddLedgerEntry,
    GroupAddExpense
}

@Composable
fun App() {
    KoinContext {
        AppTheme {
            var currentService by remember { mutableStateOf(MainService.Home) }
            var currentScreen by remember { mutableStateOf(Screen.Main) }
            var currentGroupId by remember { mutableStateOf<String?>(null) }

            Scaffold(
                topBar = {
                    if (currentScreen == Screen.Main) {
                        ServiceSelector(
                            selectedService = currentService,
                            onServiceSelected = { currentService = it }
                        )
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (currentScreen) {
                        Screen.Main -> {
                            val dashboardViewModel = koinInject<DashboardViewModel>()
                            when (currentService) {
                                MainService.Home -> HomeScreen(
                                    viewModel = dashboardViewModel,
                                    onGroupClick = { group ->
                                        currentGroupId = group.id
                                        currentScreen = Screen.GroupDetails
                                    },
                                    onSeeAllSplit = { currentService = MainService.Split },
                                    onSeeAllLedger = { currentService = MainService.OwnExpense }
                                )
                                MainService.Split -> SplitScreen(
                                    viewModel = dashboardViewModel,
                                    onGroupClick = { group ->
                                        currentGroupId = group.id
                                        currentScreen = Screen.GroupDetails
                                    },
                                    onMiscClick = { currentScreen = Screen.MiscDetails },
                                    onShowSettledClick = { currentScreen = Screen.SettledGroups },
                                    onCreateGroupClick = { currentScreen = Screen.CreateGroup },
                                    onCreateExpenseClick = { currentScreen = Screen.AddExpense }
                                )
                                MainService.OwnExpense -> OwnExpenseScreen(
                                    viewModel = dashboardViewModel,
                                    onAddExpense = { currentScreen = Screen.AddLedgerEntry }
                                )
                            }
                        }
                        Screen.AddLedgerEntry -> {
                            val dashboardViewModel = koinInject<DashboardViewModel>()
                            AddLedgerTransactionScreen(
                                viewModel = dashboardViewModel,
                                onBack = { currentScreen = Screen.Main },
                                onSuccess = { currentScreen = Screen.Main }
                            )
                        }
                        Screen.GroupDetails -> {
                            val groupId = currentGroupId!!
                            val detailsViewModel = koinInject<GroupDetailsViewModel> { parametersOf(groupId) }
                            GroupDetailsScreen(
                                viewModel = detailsViewModel,
                                onBackClick = { currentScreen = Screen.Main },
                                onShareInvite = { link -> println("Invite: $link") },
                                onAddExpenseClick = { currentScreen = Screen.GroupAddExpense }
                            )
                        }
                        Screen.MiscDetails -> {
                            val detailsViewModel = koinInject<GroupDetailsViewModel> { parametersOf("") }
                            GroupDetailsScreen(
                                viewModel = detailsViewModel,
                                onBackClick = { currentScreen = Screen.Main },
                                onShareInvite = {},
                                onAddExpenseClick = { currentScreen = Screen.GroupAddExpense }
                            )
                        }
                        Screen.SettledGroups -> {
                            val dashboardViewModel = koinInject<DashboardViewModel>()
                            SettledGroupsScreen(
                                viewModel = dashboardViewModel,
                                onBackClick = { currentScreen = Screen.Main },
                                onGroupClick = { group ->
                                    currentGroupId = group.id
                                    currentScreen = Screen.GroupDetails
                                }
                            )
                        }
                        Screen.CreateGroup -> {
                            val dashboardViewModel = koinInject<DashboardViewModel>()
                            CreateGroupScreen(
                                viewModel = dashboardViewModel,
                                onBack = { currentScreen = Screen.Main },
                                onCreated = { currentScreen = Screen.Main }
                            )
                        }
                        Screen.AddExpense -> {
                            val dashboardViewModel = koinInject<DashboardViewModel>()
                            AddExpenseScreen(
                                viewModel = dashboardViewModel,
                                onBack = { currentScreen = Screen.Main },
                                onCreateGroup = { currentScreen = Screen.CreateGroup },
                                onSuccess = { currentScreen = Screen.Main }
                            )
                        }
                        Screen.GroupAddExpense -> {
                            val groupId = currentGroupId ?: ""
                            val detailsViewModel = koinInject<GroupDetailsViewModel> { parametersOf(groupId) }
                            GroupAddExpenseScreen(
                                viewModel = detailsViewModel,
                                onBack = {
                                    currentScreen = if (groupId.isEmpty()) Screen.MiscDetails else Screen.GroupDetails
                                },
                                onSuccess = {
                                    currentScreen = if (groupId.isEmpty()) Screen.MiscDetails else Screen.GroupDetails
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceSelector(
    selectedService: MainService,
    onServiceSelected: (MainService) -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiceItem(
                name = "Patt HOME",
                isSelected = selectedService == MainService.Home,
                onClick = { onServiceSelected(MainService.Home) }
            )
            ServiceItem(
                name = "SPLIT",
                isSelected = selectedService == MainService.Split,
                onClick = { onServiceSelected(MainService.Split) }
            )
            ServiceItem(
                name = "MY LEDGER",
                isSelected = selectedService == MainService.OwnExpense,
                onClick = { onServiceSelected(MainService.OwnExpense) }
            )
        }
    }
}

@Composable
fun ServiceItem(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
            fontSize = if (isSelected) 14.sp else 12.sp
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )
        }
    }
}
