# ThePattApp (WIP)

ThePattApp is a comprehensive personal finance management and expense splitting application built with Kotlin Multiplatform (KMP) and Jetpack Compose. It combines the functionality of a private ledger for tracking personal wealth with a robust engine for splitting bills and managing group expenses.

## Core Features

### 1. Personal Wealth Ledger ("My Ledger")
*   **Multi-Account Management:** Track balances across various account types including Cash, Checking, Savings, and Credit.
*   **Hierarchical Categorization:** Log transactions using a two-tier system of Sections and Subsections for granular tracking.
*   **Net Worth Tracking:** Real-time calculation of total net worth based on assets and liabilities.
*   **Budget Monitoring:** Visual "dual-color" progress bars that track spending against time cycles, providing warnings when spending exceeds expectations.

### 2. Group Expense Splitting ("Split")
*   **Group Management:** Create and manage groups for friends, trips, or households.
*   **Advanced Splitting Logic:** Support for equal splits, exact amounts, and percentage-based allocations.
*   **Debt Simplification:** Integrated engine to minimize the number of transactions required to settle up.
*   **Miscellaneous Expenses:** A dedicated area for tracking one-off shared expenses without creating a full group.

### 3. Integrated Experience
*   **Easy Navigation:** A unified home page with a top-level service selector to switch between personal and shared finance contexts.
*   **Automated Sync:** When you pay for a shared expense, it is automatically recorded in your personal ledger, ensuring your private accounts are always up to date.
*   **Unified Activity Feed:** See all your financial movements in one place on the Home dashboard.

## Technical Architecture

*   **Kotlin Multiplatform (KMP):** Shared business logic, models, and repositories across Android and other platforms.
*   **Compose Multiplatform:** Modern, declarative UI shared across platforms.
*   **Clean Architecture:** Clear separation between Domain, Data, and Presentation layers.
*   **Dependency Injection:** Powered by Koin.
*   **Reactive UI:** Built using Kotlin Coroutines and Flow for a responsive, real-time experience.

## Getting Started

### Prerequisites
*   Android Studio (latest stable version)
*   JDK 11 or higher
*   Kotlin 1.9+

### Building the Project
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the `androidApp` configuration on an emulator or physical device.

## Privacy & Tenets
*   **Private Ledger Isolation:** All data is stored locally by default.
*   **Offline-First:** Designed to work without an internet connection for core functionality.

## TODO
1. Detailed Test suite
2. DB handling