package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Caso de uso principal de solicitud de préstamos.
 * Encapsula de forma estricta las reglas operativas de negocio (RN-01 a RN-04).
 */
class SolicitarPrestamoUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(libroId: Int, estudianteId: String): Result<Prestamo> = resultadoDe {
        // Consultar estado actual de préstamos del estudiante
        val prestamosActuales = repository.obtenerPrestamos()

        // RN-04: Un estudiante con al menos un préstamo Vencido no puede solicitar un libro nuevo hasta regularizarlo
        val tienePrestamoVencido = prestamosActuales.any { it.estado is EstadoPrestamo.Vencido }
        if (tienePrestamoVencido) {
            throw ReglaNegocioException.EstudianteConPrestamosVencidosException()
        }

        // RN-01: Un estudiante no puede tener más de tres préstamos en estado Activo de forma simultánea
        val prestamosActivos = prestamosActuales.count { it.estado is EstadoPrestamo.Activo }
        if (prestamosActivos >= 3) {
            throw ReglaNegocioException.LimitePrestamosAlcanzadoException()
        }

        // Consultar catálogo para verificar stock del libro
        val catalogo = repository.obtenerCatalogo()
        val libro = catalogo.find { it.id == libroId }
            ?: throw ReglaNegocioException.LibroNoEncontradoException()

        // RN-02: No se puede solicitar un libro cuyo número de ejemplares disponibles sea cero
        if (libro.ejemplaresDisponibles <= 0) {
            throw ReglaNegocioException.SinEjemplaresDisponiblesException()
        }

        // Realizar el registro en el repositorio (RN-03: asigna 7 días de duración)
        repository.registrarPrestamo(libroId = libroId, estudianteId = estudianteId)
    }
}
