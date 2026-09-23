package br.com.arthiviatech.myfinances.domain.usecase

import java.time.YearMonth

internal fun Int.toYearMonth(): YearMonth {
    val year = this / 100
    val month = this % 100
    return YearMonth.of(year, month)
}

internal fun YearMonth.toReferenceMonth(): Int = year * 100 + monthValue
