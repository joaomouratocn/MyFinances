package br.com.arthiviatech.myfinances.core

import java.time.LocalDate

interface AppClock {
    fun nowMillis(): Long
    fun today(): LocalDate
}

class SystemAppClock : AppClock {
    override fun nowMillis() = System.currentTimeMillis()
    override fun today() = LocalDate.now()
}
