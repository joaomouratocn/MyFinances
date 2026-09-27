package br.com.arthiviatech.myfinances.ui.future

import java.time.YearMonth

internal fun previewFutureAccount() = FutureAccountUi(1, "Energia elétrica", "Moradia", 10, "Pix", null, false)
internal fun previewFutureState() = FutureUiState(
    selectedMonth = YearMonth.of(2026, 9),
    pending = listOf(previewFutureAccount(), FutureAccountUi(2, "Água", "Moradia", 15, "Cartão de crédito", "Cartão principal", false)),
    paused = listOf(FutureAccountUi(3, "Academia", "Assinaturas e Serviços", 5, "Cartão de crédito", "Cartão principal", true)),
)
