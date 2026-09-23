package br.com.arthiviatech.myfinances.domain.usecase

import java.time.LocalDate
import java.time.YearMonth

class ResolveDateForMonthUseCase {
    operator fun invoke(month: YearMonth, dayOfMonth: Int): LocalDate {
        require(dayOfMonth in 1..31) { "O dia deve estar entre 1 e 31." }
        return month.atDay(dayOfMonth.coerceAtMost(month.lengthOfMonth()))
    }
}
