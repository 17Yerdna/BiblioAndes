package pe.upeu.biblioandes.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Implementación en memoria del repositorio de biblioteca.
 * Aislado en la capa de datos. Utiliza [Mutex] para garantizar concurrencia
 * segura y [delay] para simular latencia de carga sin bloquear hilos.
 */
class BibliotecaRepositoryFake : BibliotecaRepository {

    private val mutex = Mutex()
    private val listaLibros = mutableListOf<Libro>().apply {
        addAll(DatosSimulados.obtenerLibrosIniciales())
    }
    private val listaPrestamos = mutableListOf<Prestamo>().apply {
        addAll(DatosSimulados.obtenerPrestamosIniciales(listaLibros))
    }

    /**
     * Bandera para simular error en el catálogo según Requerimiento 3.2.
     * Puede conmutarse desde la interfaz en Perfil / Ajustes para evaluación en vivo.
     */
    var simularError: Boolean = false

    override fun alternarSimulacionError(activado: Boolean) {
        simularError = activado
    }

    override fun estaSimulandoError(): Boolean = simularError

    override suspend fun obtenerCatalogo(): List<Libro> {
        delay(800) // Retardo simulado no bloqueante (Requerimiento 3.2)
        if (simularError) {
            throw IllegalStateException("Fallo simulado: no se pudo establecer comunicación con el catálogo.")
        }
        return mutex.withLock {
            listaLibros.toList()
        }
    }

    override suspend fun obtenerPrestamos(): List<Prestamo> {
        delay(800) // Retardo simulado no bloqueante
        return mutex.withLock {
            listaPrestamos.toList()
        }
    }

    override suspend fun obtenerEstudiante(): Estudiante {
        delay(300)
        return DatosSimulados.estudiante
    }

    override suspend fun registrarPrestamo(libroId: Int, estudianteId: String): Prestamo {
        delay(800) // Simulación de procesamiento
        return mutex.withLock {
            val indiceLibro = listaLibros.indexOfFirst { it.id == libroId }
            if (indiceLibro == -1) {
                error("Libro no encontrado")
            }

            val libroActual = listaLibros[indiceLibro]
            if (libroActual.ejemplaresDisponibles <= 0) {
                error("No hay ejemplares disponibles")
            }

            // Decrementar stock
            val libroActualizado = libroActual.copy(
                ejemplaresDisponibles = libroActual.ejemplaresDisponibles - 1
            )
            listaLibros[indiceLibro] = libroActualizado

            // Generar fechas dinámicas (RN-03: 7 días de duración)
            val hoy: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val fechaPrestamo = hoy.toString()
            val fechaLimite = hoy.plus(7, DateTimeUnit.DAY).toString()

            val nuevoId = (listaPrestamos.maxOfOrNull { it.id } ?: 0) + 1
            val nuevoPrestamo = Prestamo(
                id = nuevoId,
                libro = libroActualizado,
                fechaPrestamo = fechaPrestamo,
                fechaLimite = fechaLimite,
                estado = EstadoPrestamo.Activo(diasRestantes = 7)
            )

            listaPrestamos.add(0, nuevoPrestamo)
            nuevoPrestamo
        }
    }
}
