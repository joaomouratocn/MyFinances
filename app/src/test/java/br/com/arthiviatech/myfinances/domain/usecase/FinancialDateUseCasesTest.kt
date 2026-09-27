package br.com.arthiviatech.myfinances.domain.usecase

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialDateUseCasesTest {
    private val resolveDate = ResolveDateForMonthUseCase()
    private val calculateCardDueDate = CalculateCardDueDateUseCase(resolveDate)
    private val calculateInstallmentDates = CalculateInstallmentDatesUseCase(resolveDate)

    @Test
    fun resolveDate_usesLastAvailableDayWhenRequestedDayDoesNotExist() {
        assertEquals(
            LocalDate.of(2027, 2, 28),
            resolveDate(YearMonth.of(2027, 2), 31),
        )
        assertEquals(
            LocalDate.of(2028, 2, 29),
            resolveDate(YearMonth.of(2028, 2), 31),
        )
    }

    @Test
    fun cardDueDate_beforeClosing_usesUpcomingStatementDueDate() {
        assertEquals(
            LocalDate.of(2026, 10, 5),
            calculateCardDueDate(
                purchaseDate = LocalDate.of(2026, 9, 20),
                dueDay = 5,
                closingDay = 25,
            ),
        )
    }

    @Test
    fun cardDueDate_afterClosing_movesToFollowingStatement() {
        assertEquals(
            LocalDate.of(2026, 11, 5),
            calculateCardDueDate(
                purchaseDate = LocalDate.of(2026, 9, 26),
                dueDay = 5,
                closingDay = 25,
            ),
        )
    }

    @Test
    fun cardDueDate_onClosingDay_usesUpcomingStatementDueDate() {
        assertEquals(
            LocalDate.of(2026, 10, 5),
            calculateCardDueDate(
                purchaseDate = LocalDate.of(2026, 9, 25),
                dueDay = 5,
                closingDay = 25,
            ),
        )
    }

    @Test
    fun cardWithoutClosing_usesMonthSelectedByUser() {
        assertEquals(
            LocalDate.of(2027, 2, 28),
            calculateCardDueDate(
                purchaseDate = LocalDate.of(2027, 1, 10),
                dueDay = 31,
                closingDay = null,
                selectedDueMonth = YearMonth.of(2027, 2),
            ),
        )
    }

    @Test
    fun installmentDates_keepOriginalBaseDayAcrossShortMonths() {
        assertEquals(
            listOf(
                LocalDate.of(2027, 1, 31),
                LocalDate.of(2027, 2, 28),
                LocalDate.of(2027, 3, 31),
            ),
            calculateInstallmentDates(
                firstDueDate = LocalDate.of(2027, 1, 31),
                installmentCount = 3,
            ),
        )
    }
}
