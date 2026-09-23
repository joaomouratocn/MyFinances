package br.com.arthiviatech.myfinances.domain.usecase

import java.time.LocalDate
import java.time.YearMonth

class CalculateCardDueDateUseCase(
    private val resolveDateForMonth: ResolveDateForMonthUseCase,
) {
    operator fun invoke(
        purchaseDate: LocalDate,
        dueDay: Int,
        closingDay: Int?,
        selectedDueMonth: YearMonth? = null,
    ): LocalDate {
        require(dueDay in 1..31) { "O dia de vencimento deve estar entre 1 e 31." }

        if (closingDay == null) {
            requireNotNull(selectedDueMonth) {
                "O mês do primeiro vencimento é obrigatório para cartões sem fechamento."
            }
            return resolveDateForMonth(selectedDueMonth, dueDay)
        }

        require(closingDay in 1..31) { "O dia de fechamento deve estar entre 1 e 31." }
        val purchaseMonth = YearMonth.from(purchaseDate)
        val closingInPurchaseMonth = resolveDateForMonth(purchaseMonth, closingDay)
        val statementMonth = if (purchaseDate <= closingInPurchaseMonth) {
            purchaseMonth
        } else {
            purchaseMonth.plusMonths(1)
        }
        val statementClosingDate = resolveDateForMonth(statementMonth, closingDay)
        var dueMonth = statementMonth
        var dueDate = resolveDateForMonth(dueMonth, dueDay)

        if (!dueDate.isAfter(statementClosingDate)) {
            dueMonth = dueMonth.plusMonths(1)
            dueDate = resolveDateForMonth(dueMonth, dueDay)
        }
        return dueDate
    }
}
