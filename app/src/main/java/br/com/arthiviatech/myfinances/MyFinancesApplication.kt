package br.com.arthiviatech.myfinances

import android.app.Application
import br.com.arthiviatech.myfinances.di.databaseModule
import br.com.arthiviatech.myfinances.di.repositoryModule
import br.com.arthiviatech.myfinances.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyFinancesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyFinancesApplication)
            modules(databaseModule, repositoryModule, useCaseModule)
        }
    }
}
