package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Caso de uso para obtener la lista de libros del catálogo.
 */
class ObtenerCatalogoUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): Result<List<Libro>> = resultadoDe {
        repository.obtenerCatalogo()
    }
}
