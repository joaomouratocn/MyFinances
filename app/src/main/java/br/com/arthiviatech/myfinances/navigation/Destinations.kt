package br.com.arthiviatech.myfinances.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object FutureAccounts

@Serializable
data object Reports

@Serializable
data object More

@Serializable
data object NewExpense

@Serializable
data object Revenues

@Serializable
data object Cards

@Serializable
data object Invoices

@Serializable
data object Categories
@Serializable
data class ExpenseDetails(val expenseId: Long)
