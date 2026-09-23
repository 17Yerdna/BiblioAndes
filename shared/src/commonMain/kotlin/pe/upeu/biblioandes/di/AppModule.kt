package pe.upeu.biblioandes.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase

val dataModule = module {
    single<BibliotecaRepository> { BibliotecaRepositoryFake() }
}

val domainModule = module {
    factoryOf(::ObtenerCatalogoUseCase)
    factoryOf(::ObtenerPrestamosUseCase)
    factoryOf(::SolicitarPrestamoUseCase)
}

val presentationModule = module {
    // Los ViewModels se registrarán aquí
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(platformModule, dataModule, domainModule, presentationModule)
}
