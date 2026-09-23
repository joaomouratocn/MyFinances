package br.com.arthiviatech.myfinances.di

import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.InstallmentRepository
import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.ReportRepository
import br.com.arthiviatech.myfinances.data.repository.RevenueRepository
import br.com.arthiviatech.myfinances.data.repository.impl.CardRepositoryImpl
import br.com.arthiviatech.myfinances.data.repository.impl.CategoryRepositoryImpl
import br.com.arthiviatech.myfinances.data.repository.impl.ExpenseRepositoryImpl
import br.com.arthiviatech.myfinances.data.repository.impl.InstallmentRepositoryImpl
import br.com.arthiviatech.myfinances.data.repository.impl.RecurringExpenseRepositoryImpl
import br.com.arthiviatech.myfinances.data.repository.impl.ReportRepositoryImpl
import br.com.arthiviatech.myfinances.data.repository.impl.RevenueRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<CardRepository> { CardRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single<InstallmentRepository> { InstallmentRepositoryImpl(get(), get(), get()) }
    single<RecurringExpenseRepository> { RecurringExpenseRepositoryImpl(get(), get(), get(), get()) }
    single<RevenueRepository> { RevenueRepositoryImpl(get(), get(), get(), get()) }
    single<ReportRepository> { ReportRepositoryImpl(get(), get()) }
}
