package com.happyminds.thepattapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.happyminds.thepattapp.presentation.dashboard.*
import com.happyminds.thepattapp.presentation.groupdetails.GroupAddExpenseScreen
import com.happyminds.thepattapp.presentation.groupdetails.GroupDetailsScreen
import com.happyminds.thepattapp.presentation.groupdetails.GroupDetailsViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

enum class Screen {
    Dashboard,
    GroupDetails,
    MiscDetails,
    SettledGroups,
    CreateGroup,
    AddExpense, // From Dashboard
    GroupAddExpense // From Group Details
}

@Composable
@Preview
fun App() {
    KoinContext {
        MaterialTheme {
            var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
            var currentGroupId by remember { mutableStateOf<String?>(null) }

            when (currentScreen) {
                Screen.Dashboard -> {
                    val dashboardViewModel = koinInject<DashboardViewModel>()
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onGroupClick = { group ->
                            currentGroupId = group.id
                            currentScreen = Screen.GroupDetails
                        },
                        onMiscClick = {
                            currentScreen = Screen.MiscDetails
                        },
                        onShowSettledClick = {
                            currentScreen = Screen.SettledGroups
                        },
                        onCreateGroupClick = {
                            currentScreen = Screen.CreateGroup
                        },
                        onCreateExpenseClick = {
                            currentScreen = Screen.AddExpense
                        }
                    )
                }
                Screen.GroupDetails -> {
                    val groupId = currentGroupId!!
                    val detailsViewModel = koinInject<GroupDetailsViewModel> { parametersOf(groupId) }
                    GroupDetailsScreen(
                        viewModel = detailsViewModel,
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onShareInvite = { link -> println("Invite: $link") },
                        onAddExpenseClick = {
                            currentScreen = Screen.GroupAddExpense
                        }
                    )
                }
                Screen.MiscDetails -> {
                    val detailsViewModel = koinInject<GroupDetailsViewModel> { parametersOf("") }
                    GroupDetailsScreen(
                        viewModel = detailsViewModel,
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
                        onShareInvite = {},
                        onAddExpenseClick = {
                            currentScreen = Screen.GroupAddExpense
                        }
                    )
                }
                Screen.SettledGroups -> {
                    val dashboardViewModel = koinInject<DashboardViewModel>()
                    SettledGroupsScreen(
                        viewModel = dashboardViewModel,
                        onBackClick = {
                            currentScreen = Screen.Dashboard
                        },
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
                        onBack = { currentScreen = Screen.Dashboard },
                        onCreated = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.AddExpense -> {
                    val dashboardViewModel = koinInject<DashboardViewModel>()
                    AddExpenseScreen(
                        viewModel = dashboardViewModel,
                        onBack = { currentScreen = Screen.Dashboard },
                        onCreateGroup = { currentScreen = Screen.CreateGroup },
                        onSuccess = { currentScreen = Screen.Dashboard }
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
