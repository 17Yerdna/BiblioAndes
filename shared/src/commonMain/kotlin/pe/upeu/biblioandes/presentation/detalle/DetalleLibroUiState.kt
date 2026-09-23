package pe.upeu.biblioandes.presentation.detalle

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

data class DetalleLibroUiState(
    val libro: Libro? = null,
    val cargando: Boolean = true,
    val solicitando: Boolean = false,
    val mostrarDialogoConfirmacion: Boolean = false,
    val prestamoExitoso: Prestamo? = null,
    val error: String? = null
)
