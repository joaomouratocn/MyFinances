package br.com.arthiviatech.myfinances.ui.input

import org.junit.Assert.assertEquals
import org.junit.Test

class DateInputFormatterTest {
    @Test
    fun insertsSeparatorsWhileTypingDigits() {
        assertEquals("05/10/2026", formatDateInput("05102026"))
        assertEquals("05/10/2026", formatDateInput("05/10/2026"))
    }
}
