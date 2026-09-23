package br.com.arthiviatech.myfinances.di

import br.com.arthiviatech.myfinances.domain.usecase.CalculateCardDueDateUseCase
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
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
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
    viewModel { HomeViewModel(get(), get(), get()) }
}
