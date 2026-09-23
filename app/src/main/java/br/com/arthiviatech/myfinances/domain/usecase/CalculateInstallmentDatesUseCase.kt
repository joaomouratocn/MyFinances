package br.com.arthiviatech.myfinances.domain.usecase

import java.time.LocalDate
import java.time.YearMonth

class CalculateInstallmentDatesUseCase(
    private val resolveDateForMonth: ResolveDateForMonthUseCase,
) {
    operator fun invoke(firstDueDate: LocalDate, installmentCount: Int): List<LocalDate> {
        require(installmentCount > 0) { "A quantidade de parcelas deve ser maior que zero." }
        val dueDay = firstDueDate.dayOfMonth
        val firstMonth = YearMonth.from(firstDueDate)

        return List(installmentCount) { index ->
            resolveDateForMonth(firstMonth.plusMonths(index.toLong()), dueDay)
        }
    }
}
