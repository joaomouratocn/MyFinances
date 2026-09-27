package br.com.arthiviatech.myfinances.ui.input

/** Keeps date fields in dd/MM/yyyy while the user types. */
fun formatDateInput(value: String): String {
    val digits = value.filter(Char::isDigit).take(8)
    return buildString {
        digits.forEachIndexed { index, digit ->
            if (index == 2 || index == 4) append('/')
            append(digit)
        }
    }
}
