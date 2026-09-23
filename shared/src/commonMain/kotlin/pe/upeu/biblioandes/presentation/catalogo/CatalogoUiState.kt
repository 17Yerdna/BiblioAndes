package pe.upeu.biblioandes.presentation.catalogo

import pe.upeu.biblioandes.domain.model.Libro

/**
 * Fases de estado del catálogo (Carga, Contenido, Vacío y Error).
 */
sealed interface FaseCatalogo {
    data object Cargando : FaseCatalogo
    data object Vacio : FaseCatalogo
    data class Contenido(val libros: List<Libro>) : FaseCatalogo
    data class Error(val mensaje: String) : FaseCatalogo
}

/**
 * Estado observable completo de la pantalla de Catálogo.
 */
data class CatalogoUiState(
    val fase: FaseCatalogo = FaseCatalogo.Cargando,
    val categorias: List<String> = emptyList(),
    val categoriaSeleccionada: String? = null,
    val textoBusqueda: String = "",
    val librosFiltrados: List<Libro> = emptyList()
)
