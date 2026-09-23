package br.com.arthiviatech.myfinances.ui.home

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale

internal val HomeSuccess = Color(0xFF146C2E)

internal fun formatCurrency(cents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cents / 100.0)
