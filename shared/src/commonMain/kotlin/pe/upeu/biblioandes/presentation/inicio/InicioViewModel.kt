package pe.upeu.biblioandes.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

class InicioViewModel(
    private val repository: BibliotecaRepository,
    private val obtenerPrestamosUseCase: ObtenerPrestamosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        cargarInicio()
    }

    fun cargarInicio() {
        _uiState.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val estudiante = repository.obtenerEstudiante()
                val resultadoPrestamos = obtenerPrestamosUseCase()

                resultadoPrestamos.fold(
                    onSuccess = { prestamos ->
                        val activos = prestamos.filter { it.estado is EstadoPrestamo.Activo }
                        val vencidos = prestamos.filter { it.estado is EstadoPrestamo.Vencido }

                        // El préstamo cuya devolución vence primero
                        val proximo = activos.minByOrNull { prestamo ->
                            (prestamo.estado as EstadoPrestamo.Activo).diasRestantes
                        }

                        _uiState.update {
                            it.copy(
                                estudiante = estudiante,
                                prestamoProximoVencer = proximo,
                                cantidadPrestamosActivos = activos.size,
                                cantidadPrestamosVencidos = vencidos.size,
                                cargando = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                estudiante = estudiante,
                                cargando = false,
                                error = error.message
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = e.message ?: "Error al cargar la información inicial"
                    )
                }
            }
        }
    }
}
