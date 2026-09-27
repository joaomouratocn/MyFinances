package br.com.arthiviatech.myfinances.ui.home

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeExpenseFiltersTest {
    private val expenses = listOf(
        expense(1, "Conta atrasada recente", 3000, "2026-09-08", ExpenseStatus.OVERDUE, 1, null),
        expense(2, "Conta paga", 5000, "2026-09-20", ExpenseStatus.PAID, 2, 1),
        expense(3, "Conta pendente", 2000, "2026-09-15", ExpenseStatus.PENDING, 1, 1),
        expense(4, "Conta atrasada antiga", 4000, "2026-09-05", ExpenseStatus.OVERDUE, 2, null),
    )

    @Test
    fun defaultOrder_placesOverdueThenPendingThenPaid() {
        assertEquals(listOf(4L, 1L, 3L, 2L), expenses.filterAndSort("", HomeExpenseFilters()).map { it.id })
    }

    @Test
    fun filtersCombineDescriptionPeriodCategoryCardAndStatus() {
        val filters = HomeExpenseFilters(
            startDate = "10/09/2026",
            endDate = "30/09/2026",
            categoryId = 1,
            cardId = 1,
            status = ExpenseStatus.PENDING,
        )
        assertEquals(listOf(3L), expenses.filterAndSort("pendente", filters).map { it.id })
    }

    @Test
    fun valueDescendingOrder_usesLargestValueFirst() {
        assertEquals(
            listOf(2L, 4L, 1L, 3L),
            expenses.filterAndSort("", HomeExpenseFilters(sort = HomeExpenseSort.VALUE_DESC)).map { it.id },
        )
    }

    private fun expense(
        id: Long, description: String, amount: Long, date: String,
        status: ExpenseStatus, categoryId: Long, cardId: Long?,
    ) = HomeExpenseUi(
        id = id,
        description = description,
        amountCents = amount,
        category = "Categoria",
        categoryId = categoryId,
        dueDate = LocalDate.parse(date),
        cardId = cardId,
        status = status,
    )
}
