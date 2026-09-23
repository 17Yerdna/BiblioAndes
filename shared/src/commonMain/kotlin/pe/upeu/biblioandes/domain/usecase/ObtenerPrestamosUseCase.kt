package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Caso de uso para obtener los préstamos del estudiante.
 * Ordena la lista priorizando la fecha de devolución más próxima (RF-04).
 */
class ObtenerPrestamosUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): Result<List<Prestamo>> = resultadoDe {
        val prestamos = repository.obtenerPrestamos()

        // Ordenamiento por fecha límite más próxima (RF-04)
        // Activos primero por fechaLimite ascendente, luego vencidos, luego devueltos
        prestamos.sortedWith(
            compareBy<Prestamo> { prestamo ->
                when (prestamo.estado) {
                    is EstadoPrestamo.Activo -> 0
                    is EstadoPrestamo.Vencido -> 1
                    is EstadoPrestamo.Devuelto -> 2
                }
            }.thenBy { it.fechaLimite }
        )
    }
}
