package br.com.arthiviatech.myfinances.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.arthiviatech.myfinances.data.room.entitys.CategoryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseInstrumentedTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun disabledExpenseIsHiddenFromMonthButRetainedForAudit() = runBlocking {
        val categoryId = database.categoryDao().insert(CategoryEntity(name = "Teste"))
        val expenseId = database.expenseEntryDao().insert(
            ExpenseEntryEntity(
                description = "Compra de teste",
                amountCents = 1_000,
                categoryId = categoryId,
                purchaseDateEpochDay = 20_000,
                dueDateEpochDay = 20_000,
                referenceMonth = 202609,
                paymentMethod = "PIX",
                origin = "EVENTUAL",
            ),
        )

        assertEquals(1, database.expenseEntryDao().observeByMonth(202609).first().size)
        assertEquals(1, database.expenseEntryDao().disable(expenseId, 123L))

        assertFalse(database.expenseEntryDao().observeByMonth(202609).first().isNotEmpty())
        assertNotNull(database.expenseEntryDao().findById(expenseId))
    }
}
