package pe.upeu.biblioandes.presentation.inicio

import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo

data class InicioUiState(
    val estudiante: Estudiante? = null,
    val prestamoProximoVencer: Prestamo? = null,
    val cantidadPrestamosActivos: Int = 0,
    val cantidadPrestamosVencidos: Int = 0,
    val cargando: Boolean = true,
    val error: String? = null
)
