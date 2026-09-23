package pe.upeu.biblioandes.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.presentation.util.contieneTermino

class CatalogoViewModel(
    private val obtenerCatalogoUseCase: ObtenerCatalogoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    private var todosLosLibros: List<Libro> = emptyList()

    init {
        cargarCatalogo()
    }

    fun cargarCatalogo() {
        _uiState.update { it.copy(fase = FaseCatalogo.Cargando) }

        viewModelScope.launch {
            val resultado = obtenerCatalogoUseCase()
            resultado.fold(
                onSuccess = { libros ->
                    todosLosLibros = libros
                    val categorias = libros.map { it.categoria }.distinct().sorted()

                    _uiState.update { estado ->
                        val filtrados = aplicarFiltros(libros, estado.categoriaSeleccionada, estado.textoBusqueda)
                        estado.copy(
                            fase = if (filtrados.isEmpty()) FaseCatalogo.Vacio else FaseCatalogo.Contenido(filtrados),
                            categorias = categorias,
                            librosFiltrados = filtrados
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(fase = FaseCatalogo.Error(error.message ?: "No se pudo cargar el catálogo."))
                    }
                }
            )
        }
    }

    fun seleccionarCategoria(categoria: String?) {
        val nuevaCategoria = if (_uiState.value.categoriaSeleccionada == categoria) null else categoria
        _uiState.update { estado ->
            val filtrados = aplicarFiltros(todosLosLibros, nuevaCategoria, estado.textoBusqueda)
            estado.copy(
                categoriaSeleccionada = nuevaCategoria,
                librosFiltrados = filtrados,
                fase = if (filtrados.isEmpty()) FaseCatalogo.Vacio else FaseCatalogo.Contenido(filtrados)
            )
        }
    }

    fun actualizarBusqueda(query: String) {
        _uiState.update { estado ->
            val filtrados = aplicarFiltros(todosLosLibros, estado.categoriaSeleccionada, query)
            estado.copy(
                textoBusqueda = query,
                librosFiltrados = filtrados,
                fase = if (filtrados.isEmpty()) FaseCatalogo.Vacio else FaseCatalogo.Contenido(filtrados)
            )
        }
    }

    private fun aplicarFiltros(
        libros: List<Libro>,
        categoria: String?,
        busqueda: String
    ): List<Libro> {
        return libros.filter { libro ->
            val coincideCategoria = categoria == null || libro.categoria.equals(categoria, ignoreCase = true)
            val coincideBusqueda = busqueda.isBlank() ||
                    libro.titulo.contieneTermino(busqueda) ||
                    libro.autor.contieneTermino(busqueda)

            coincideCategoria && coincideBusqueda
        }
    }
}
