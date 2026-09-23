package br.com.arthiviatech.myfinances.di

import androidx.room.Room
import br.com.arthiviatech.myfinances.data.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase>(createdAtStart = true) {
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME,
        ).build()
    }

    single { get<AppDatabase>().cardDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().expenseEntryDao() }
    single { get<AppDatabase>().fixedRevenueDao() }
    single { get<AppDatabase>().fixedRevenueVersionDao() }
    single { get<AppDatabase>().installmentPlanDao() }
    single { get<AppDatabase>().recurrencePauseDao() }
    single { get<AppDatabase>().recurringExpenseDao() }
    single { get<AppDatabase>().revenueEntryDao() }
}
