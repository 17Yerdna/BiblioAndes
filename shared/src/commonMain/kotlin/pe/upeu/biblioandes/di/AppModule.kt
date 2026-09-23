package pe.upeu.biblioandes.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val dataModule = module {
}

val domainModule = module {
}

val presentationModule = module {
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(platformModule, dataModule, domainModule, presentationModule)
}
