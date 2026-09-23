package pe.upeu.biblioandes.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase

class DetalleLibroViewModel(
    private val obtenerCatalogoUseCase: ObtenerCatalogoUseCase,
    private val solicitarPrestamoUseCase: SolicitarPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleLibroUiState())
    val uiState: StateFlow<DetalleLibroUiState> = _uiState.asStateFlow()

    fun cargarDetalle(libroId: Int) {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            val resultado = obtenerCatalogoUseCase()
            resultado.fold(
                onSuccess = { catalogo ->
                    val libro = catalogo.find { it.id == libroId }
                    if (libro != null) {
                        _uiState.update { it.copy(libro = libro, cargando = false) }
                    } else {
                        _uiState.update { it.copy(error = "Libro no encontrado", cargando = false) }
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message, cargando = false) }
                }
            )
        }
    }

    fun abrirDialogoConfirmacion() {
        _uiState.update { it.copy(mostrarDialogoConfirmacion = true, error = null) }
    }

    fun cerrarDialogoConfirmacion() {
        _uiState.update { it.copy(mostrarDialogoConfirmacion = false) }
    }

    fun confirmarSolicitudPrestamo(estudianteId: String = "E-2291") {
        val libro = _uiState.value.libro ?: return
        if (_uiState.value.solicitando) return // Prevención de doble pulsación

        _uiState.update {
            it.copy(solicitando = true, mostrarDialogoConfirmacion = false, error = null)
        }

        viewModelScope.launch {
            val resultado = solicitarPrestamoUseCase(libroId = libro.id, estudianteId = estudianteId)
            resultado.fold(
                onSuccess = { nuevoPrestamo ->
                    // Actualizar el stock del libro localmente
                    val libroActualizado = libro.copy(
                        ejemplaresDisponibles = (libro.ejemplaresDisponibles - 1).coerceAtLeast(0)
                    )
                    _uiState.update {
                        it.copy(
                            libro = libroActualizado,
                            solicitando = false,
                            prestamoExitoso = nuevoPrestamo,
                            error = null
                        )
                    }
                },
                onFailure = { excepcion ->
                    _uiState.update {
                        it.copy(
                            solicitando = false,
                            error = excepcion.message ?: "No se pudo procesar el préstamo."
                        )
                    }
                }
            )
        }
    }

    fun limpiarMensajeExito() {
        _uiState.update { it.copy(prestamoExitoso = null) }
    }
}
