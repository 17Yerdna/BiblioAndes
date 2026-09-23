package pe.upeu.biblioandes

import kotlinx.coroutines.runBlocking
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.ReglaNegocioException
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import kotlin.test.Test
import kotlin.test.assertTrue

class FakeTestRepository(
    var libros: List<Libro> = emptyList(),
    var prestamos: List<Prestamo> = emptyList()
) : BibliotecaRepository {
    override suspend fun obtenerCatalogo(): List<Libro> = libros
    override suspend fun obtenerPrestamos(): List<Prestamo> = prestamos
    override suspend fun obtenerEstudiante(): Estudiante = Estudiante("1", "Test", "Sistemas", "test@test.pe")
    override suspend fun registrarPrestamo(libroId: Int, estudianteId: String): Prestamo {
        val libro = libros.first { it.id == libroId }
        val nuevoPrestamo = Prestamo(100, libro, "2026-09-23", "2026-09-30", EstadoPrestamo.Activo(7))
        prestamos = listOf(nuevoPrestamo) + prestamos
        return nuevoPrestamo
    }
    override fun alternarSimulacionError(activado: Boolean) {}
    override fun estaSimulandoError(): Boolean = false
}

class SolicitarPrestamoUseCaseTest {

    private val libroDisponible = Libro(1, "Kotlin", "Autor", 2023, "Programación", "Central", 2)
    private val libroSinStock = Libro(2, "Java", "Autor", 2020, "Programación", "Central", 0)

    @Test
    fun rechaza_solicitud_si_estudiante_tiene_prestamo_vencido_RN04() = runBlocking {
        val repo = FakeTestRepository(
            libros = listOf(libroDisponible),
            prestamos = listOf(
                Prestamo(1, libroDisponible, "2026-08-01", "2026-08-08", EstadoPrestamo.Vencido(10))
            )
        )
        val useCase = SolicitarPrestamoUseCase(repo)

        val resultado = useCase(libroId = 1, estudianteId = "E-1")

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull() is ReglaNegocioException.EstudianteConPrestamosVencidosException)
    }

    @Test
    fun rechaza_solicitud_si_estudiante_tiene_3_prestamos_activos_RN01() = runBlocking {
        val repo = FakeTestRepository(
            libros = listOf(libroDisponible),
            prestamos = listOf(
                Prestamo(1, libroDisponible, "2026-09-20", "2026-09-27", EstadoPrestamo.Activo(4)),
                Prestamo(2, libroDisponible, "2026-09-21", "2026-09-28", EstadoPrestamo.Activo(5)),
                Prestamo(3, libroDisponible, "2026-09-22", "2026-09-29", EstadoPrestamo.Activo(6))
            )
        )
        val useCase = SolicitarPrestamoUseCase(repo)

        val resultado = useCase(libroId = 1, estudianteId = "E-1")

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull() is ReglaNegocioException.LimitePrestamosAlcanzadoException)
    }

    @Test
    fun rechaza_solicitud_si_libro_no_tiene_ejemplares_disponibles_RN02() = runBlocking {
        val repo = FakeTestRepository(
            libros = listOf(libroSinStock),
            prestamos = emptyList()
        )
        val useCase = SolicitarPrestamoUseCase(repo)

        val resultado = useCase(libroId = 2, estudianteId = "E-1")

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull() is ReglaNegocioException.SinEjemplaresDisponiblesException)
    }

    @Test
    fun permite_solicitar_si_cumple_todas_las_reglas() = runBlocking {
        val repo = FakeTestRepository(
            libros = listOf(libroDisponible),
            prestamos = listOf(
                Prestamo(1, libroDisponible, "2026-09-20", "2026-09-27", EstadoPrestamo.Activo(4))
            )
        )
        val useCase = SolicitarPrestamoUseCase(repo)

        val resultado = useCase(libroId = 1, estudianteId = "E-1")

        assertTrue(resultado.isSuccess)
    }
}
