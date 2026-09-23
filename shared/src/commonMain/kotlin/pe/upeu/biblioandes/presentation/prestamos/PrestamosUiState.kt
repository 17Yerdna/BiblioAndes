package pe.upeu.biblioandes.presentation.prestamos

import pe.upeu.biblioandes.domain.model.Prestamo

enum class FiltroEstadoPrestamo(val etiqueta: String) {
    TODOS("Todos"),
    ACTIVOS("Activos"),
    DEVUELTOS("Devueltos"),
    VENCIDOS("Vencidos")
}

sealed interface FasePrestamos {
    data object Cargando : FasePrestamos
    data object Vacio : FasePrestamos
    data class Contenido(val prestamos: List<Prestamo>) : FasePrestamos
    data class Error(val mensaje: String) : FasePrestamos
}

data class PrestamosUiState(
    val fase: FasePrestamos = FasePrestamos.Cargando,
    val filtroSeleccionado: FiltroEstadoPrestamo = FiltroEstadoPrestamo.TODOS,
    val prestamosFiltrados: List<Prestamo> = emptyList()
)
