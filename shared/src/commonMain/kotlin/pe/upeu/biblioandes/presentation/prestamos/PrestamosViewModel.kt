package pe.upeu.biblioandes.presentation.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

class PrestamosViewModel(
    private val obtenerPrestamosUseCase: ObtenerPrestamosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamosUiState())
    val uiState: StateFlow<PrestamosUiState> = _uiState.asStateFlow()

    private var todosLosPrestamos: List<Prestamo> = emptyList()

    init {
        cargarPrestamos()
    }

    fun cargarPrestamos() {
        _uiState.update { it.copy(fase = FasePrestamos.Cargando) }

        viewModelScope.launch {
            val resultado = obtenerPrestamosUseCase()
            resultado.fold(
                onSuccess = { lista ->
                    todosLosPrestamos = lista
                    val filtrados = aplicarFiltro(lista, _uiState.value.filtroSeleccionado)
                    _uiState.update {
                        it.copy(
                            fase = if (filtrados.isEmpty()) FasePrestamos.Vacio else FasePrestamos.Contenido(filtrados),
                            prestamosFiltrados = filtrados
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            fase = FasePrestamos.Error(error.message ?: "No se pudieron cargar los préstamos.")
                        )
                    }
                }
            )
        }
    }

    fun cambiarFiltro(nuevoFiltro: FiltroEstadoPrestamo) {
        _uiState.update { estado ->
            val filtrados = aplicarFiltro(todosLosPrestamos, nuevoFiltro)
            estado.copy(
                filtroSeleccionado = nuevoFiltro,
                prestamosFiltrados = filtrados,
                fase = if (filtrados.isEmpty()) FasePrestamos.Vacio else FasePrestamos.Contenido(filtrados)
            )
        }
    }

    private fun aplicarFiltro(
        lista: List<Prestamo>,
        filtro: FiltroEstadoPrestamo
    ): List<Prestamo> {
        return when (filtro) {
            FiltroEstadoPrestamo.TODOS -> lista
            FiltroEstadoPrestamo.ACTIVOS -> lista.filter { it.estado is EstadoPrestamo.Activo }
            FiltroEstadoPrestamo.DEVUELTOS -> lista.filter { it.estado is EstadoPrestamo.Devuelto }
            FiltroEstadoPrestamo.VENCIDOS -> lista.filter { it.estado is EstadoPrestamo.Vencido }
        }
    }
}
