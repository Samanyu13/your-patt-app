package com.happyminds.thepattapp.di

import com.happyminds.thepattapp.data.remote.CurrencyApi
import com.happyminds.thepattapp.data.remote.createHttpClient
import com.happyminds.thepattapp.data.repository.MockExpenseRepository
import com.happyminds.thepattapp.domain.repository.ExpenseRepository
import com.happyminds.thepattapp.domain.services.*
import com.happyminds.thepattapp.presentation.dashboard.DashboardViewModel
import com.happyminds.thepattapp.presentation.groupdetails.GroupDetailsViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single { CurrencyApi(get()) }
    single { CurrencyConverter(get()) }
    single { DeepLinkService() }
    single<OcrService> { MockOcrService() }
    single<ExpenseRepository> { MockExpenseRepository() }
    single { DebtSimplifier() }
    single { AccountEscalator(get()) }
    single { SplitCalculator() }
    factory { DashboardViewModel(get()) }
    factory { (groupId: String) -> GroupDetailsViewModel(groupId, get(), get(), get(), get()) }
}

fun initKoin(appDeclaration: org.koin.dsl.KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }

// For iOS
fun initKoin() = initKoin {}
