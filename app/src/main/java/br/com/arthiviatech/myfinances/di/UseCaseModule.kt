package br.com.arthiviatech.myfinances.di

import br.com.arthiviatech.myfinances.domain.usecase.CalculateCardDueDateUseCase
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.core.SystemAppClock
import br.com.arthiviatech.myfinances.domain.usecase.CalculateInstallmentDatesUseCase
import br.com.arthiviatech.myfinances.domain.usecase.CalculateMonthlyBalanceUseCase
import br.com.arthiviatech.myfinances.domain.usecase.ConfirmRecurringExpenseForMonthUseCase
import br.com.arthiviatech.myfinances.domain.usecase.CreateInstallmentPlanUseCase
import br.com.arthiviatech.myfinances.domain.usecase.DisableInstallmentsFromUseCase
import br.com.arthiviatech.myfinances.domain.usecase.MaterializeFixedRevenuesUseCase
import br.com.arthiviatech.myfinances.domain.usecase.PauseRecurringExpenseUseCase
import br.com.arthiviatech.myfinances.domain.usecase.ResolveDateForMonthUseCase
import br.com.arthiviatech.myfinances.domain.usecase.ResumeRecurringExpenseUseCase
import br.com.arthiviatech.myfinances.domain.usecase.UpdateFutureFixedRevenueUseCase
import br.com.arthiviatech.myfinances.ui.home.HomeViewModel
import br.com.arthiviatech.myfinances.ui.expense.NewExpenseViewModel
import br.com.arthiviatech.myfinances.ui.expense.ExpenseDetailViewModel
import br.com.arthiviatech.myfinances.ui.categories.CategoriesViewModel
import br.com.arthiviatech.myfinances.ui.cards.CardsViewModel
import br.com.arthiviatech.myfinances.ui.revenue.RevenueViewModel
import br.com.arthiviatech.myfinances.ui.reports.ReportsViewModel
import br.com.arthiviatech.myfinances.ui.invoices.InvoicesViewModel
import br.com.arthiviatech.myfinances.ui.future.FutureViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    single<AppClock> { SystemAppClock() }
    single { ResolveDateForMonthUseCase() }
    single { CalculateCardDueDateUseCase(get()) }
    single { CalculateInstallmentDatesUseCase(get()) }
    factory { CreateInstallmentPlanUseCase(get(), get()) }
    factory { ConfirmRecurringExpenseForMonthUseCase(get(), get(), get()) }
    factory { PauseRecurringExpenseUseCase(get()) }
    factory { ResumeRecurringExpenseUseCase(get()) }
    factory { MaterializeFixedRevenuesUseCase(get(), get()) }
    factory { UpdateFutureFixedRevenueUseCase(get()) }
    factory { CalculateMonthlyBalanceUseCase(get()) }
    factory { DisableInstallmentsFromUseCase(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { NewExpenseViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ExpenseDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { CategoriesViewModel(get(), get()) }
    viewModel { CardsViewModel(get(), get()) }
    viewModel { RevenueViewModel(get(), get(), get(), get()) }
    viewModel { ReportsViewModel(get(), get(), get(), get()) }
    viewModel { InvoicesViewModel(get(), get(), get()) }
    viewModel { FutureViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
