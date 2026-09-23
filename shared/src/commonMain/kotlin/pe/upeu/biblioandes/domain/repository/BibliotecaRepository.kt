package pe.upeu.biblioandes.domain.repository

import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Contrato de repositorio puro del dominio.
 * No depende de ninguna biblioteca externa ni tecnología de persistencia.
 */
interface BibliotecaRepository {
    suspend fun obtenerCatalogo(): List<Libro>
    suspend fun obtenerPrestamos(): List<Prestamo>
    suspend fun obtenerEstudiante(): Estudiante
    suspend fun registrarPrestamo(libroId: Int, estudianteId: String): Prestamo
    fun alternarSimulacionError(activado: Boolean)
    fun estaSimulandoError(): Boolean
}
