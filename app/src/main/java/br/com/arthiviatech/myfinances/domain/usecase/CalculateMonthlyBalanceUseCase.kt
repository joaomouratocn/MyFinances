package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.ReportRepository

class CalculateMonthlyBalanceUseCase(
    private val reportRepository: ReportRepository,
) {
    operator fun invoke(referenceMonth: Int) = reportRepository.observeMonthlySummary(referenceMonth)
}
