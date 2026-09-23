package pe.upeu.biblioandes.data.local

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Fuente de datos simulada en memoria.
 * Cumple estrictamente con los datos entregados en el Anexo del Examen Parcial
 * y genera fechas dinámicas relativas a la fecha actual del sistema para asegurar
 * que los préstamos Activos queden siempre en el futuro durante la evaluación.
 */
object DatosSimulados {

    val estudiante = Estudiante(
        id = "E-2291",
        nombre = "Diego Huamán Ccama",
        carrera = "Ingeniería de Sistemas",
        correo = "diego.huaman@correo.pe"
    )

    val categorias = listOf(
        "Programación",
        "Matemática",
        "Redes",
        "Gestión",
        "Literatura"
    )

    fun obtenerLibrosIniciales(): List<Libro> = listOf(
        Libro(1, "Kotlin en profundidad", "M. Salazar", 2023, "Programación", "Central", 3),
        Libro(2, "Estructuras de datos", "R. Peña", 2021, "Programación", "Central", 0),
        Libro(3, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2),
        Libro(4, "Redes de computadoras", "A. Medina", 2022, "Redes", "Sede Sur", 4),
        Libro(5, "Seguridad en redes", "P. Ríos", 2024, "Redes", "Central", 0),
        Libro(6, "Gestión de proyectos", "S. Delgado", 2021, "Gestión", "Sede Norte", 2),
        Libro(7, "Arquitectura Limpia en KMP", "R. Martin", 2023, "Programación", "Central", 5),
        Libro(8, "Álgebra Lineal y Aplicaciones", "G. Strang", 2020, "Matemática", "Sede Sur", 3),
        Libro(9, "Enrutamiento TCP/IP Avanzado", "J. Kurose", 2023, "Redes", "Central", 2),
        Libro(10, "Liderazgo de Equipos Ágiles", "J. Kotter", 2022, "Gestión", "Sede Sur", 4),
        Libro(11, "Cien Años de Soledad", "G. García Márquez", 2017, "Literatura", "Central", 6),
        Libro(12, "Poesía Completa de Vallejo", "C. Vallejo", 2015, "Literatura", "Sede Norte", 3)
    )

    fun obtenerPrestamosIniciales(libros: List<Libro>): List<Prestamo> {
        val hoy: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val p1Inicio = hoy.minus(2, DateTimeUnit.DAY).toString()
        val p1Fin = hoy.plus(5, DateTimeUnit.DAY).toString()

        val p2Inicio = hoy.minus(1, DateTimeUnit.DAY).toString()
        val p2Fin = hoy.plus(6, DateTimeUnit.DAY).toString()

        val p3Inicio = hoy.minus(35, DateTimeUnit.DAY).toString()
        val p3Fin = hoy.minus(28, DateTimeUnit.DAY).toString()
        val p3Dev = hoy.minus(29, DateTimeUnit.DAY).toString()

        val p4Inicio = hoy.minus(50, DateTimeUnit.DAY).toString()
        val p4Fin = hoy.minus(43, DateTimeUnit.DAY).toString()
        val p4Dev = hoy.minus(44, DateTimeUnit.DAY).toString()

        val p5Inicio = hoy.minus(25, DateTimeUnit.DAY).toString()
        val p5Fin = hoy.minus(18, DateTimeUnit.DAY).toString()

        return listOf(
            Prestamo(
                id = 1,
                libro = libros[0], // Kotlin en profundidad
                fechaPrestamo = p1Inicio,
                fechaLimite = p1Fin,
                estado = EstadoPrestamo.Activo(diasRestantes = 5)
            ),
            Prestamo(
                id = 2,
                libro = libros[3], // Redes de computadoras
                fechaPrestamo = p2Inicio,
                fechaLimite = p2Fin,
                estado = EstadoPrestamo.Activo(diasRestantes = 6)
            ),
            Prestamo(
                id = 3,
                libro = libros[2], // Cálculo aplicado
                fechaPrestamo = p3Inicio,
                fechaLimite = p3Fin,
                estado = EstadoPrestamo.Devuelto(fechaDevolucion = p3Dev)
            ),
            Prestamo(
                id = 4,
                libro = libros[1], // Estructuras de datos
                fechaPrestamo = p4Inicio,
                fechaLimite = p4Fin,
                estado = EstadoPrestamo.Devuelto(fechaDevolucion = p4Dev)
            ),
            Prestamo(
                id = 5,
                libro = libros[5], // Gestión de proyectos
                fechaPrestamo = p5Inicio,
                fechaLimite = p5Fin,
                estado = EstadoPrestamo.Vencido(diasDeAtraso = 18)
            )
        )
    }
}
