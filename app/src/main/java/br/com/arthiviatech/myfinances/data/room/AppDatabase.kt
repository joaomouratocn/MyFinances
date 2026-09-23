package br.com.arthiviatech.myfinances.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.arthiviatech.myfinances.data.room.daos.CardDao
import br.com.arthiviatech.myfinances.data.room.daos.CategoryDao
import br.com.arthiviatech.myfinances.data.room.daos.ExpenseEntryDao
import br.com.arthiviatech.myfinances.data.room.daos.FixedRevenueDao
import br.com.arthiviatech.myfinances.data.room.daos.FixedRevenueVersionDao
import br.com.arthiviatech.myfinances.data.room.daos.InstallmentPlanDao
import br.com.arthiviatech.myfinances.data.room.daos.RecurrencePauseDao
import br.com.arthiviatech.myfinances.data.room.daos.RecurringExpenseDao
import br.com.arthiviatech.myfinances.data.room.daos.RevenueEntryDao
import br.com.arthiviatech.myfinances.data.room.entitys.CardEntity
import br.com.arthiviatech.myfinances.data.room.entitys.CategoryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueEntity
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueVersionEntity
import br.com.arthiviatech.myfinances.data.room.entitys.InstallmentPlanEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurrencePauseEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurringExpenseEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RevenueEntryEntity

@Database(
    entities = [
        CardEntity::class,
        CategoryEntity::class,
        ExpenseEntryEntity::class,
        FixedRevenueEntity::class,
        FixedRevenueVersionEntity::class,
        InstallmentPlanEntity::class,
        RecurrencePauseEntity::class,
        RecurringExpenseEntity::class,
        RevenueEntryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun categoryDao(): CategoryDao

    abstract fun expenseEntryDao(): ExpenseEntryDao

    abstract fun fixedRevenueDao(): FixedRevenueDao

    abstract fun fixedRevenueVersionDao(): FixedRevenueVersionDao

    abstract fun installmentPlanDao(): InstallmentPlanDao

    abstract fun recurrencePauseDao(): RecurrencePauseDao

    abstract fun recurringExpenseDao(): RecurringExpenseDao

    abstract fun revenueEntryDao(): RevenueEntryDao

    companion object {
        const val DATABASE_NAME = "my_finances.db"
    }
}
