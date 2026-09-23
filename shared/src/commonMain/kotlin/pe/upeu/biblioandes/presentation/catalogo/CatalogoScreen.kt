package pe.upeu.biblioandes.presentation.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoError
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.theme.ColorAgotado
import pe.upeu.biblioandes.presentation.theme.ColorDisponible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onLibroClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Encabezado
        Text(
            text = "Catálogo de Libros",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Explora y consulta disponibilidad de ejemplares",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // RF-05: Campo de búsqueda tolerante a mayúsculas y tildes
        OutlinedTextField(
            value = uiState.textoBusqueda,
            onValueChange = { viewModel.actualizarBusqueda(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por título o autor...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (uiState.textoBusqueda.isNotEmpty()) {
                    IconButton(onClick = { viewModel.actualizarBusqueda("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // RF-02: Filtro por categorías con chips horizontales
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.categoriaSeleccionada == null,
                onClick = { viewModel.seleccionarCategoria(null) },
                label = { Text("Todas") }
            )
            uiState.categorias.forEach { categoria ->
                FilterChip(
                    selected = uiState.categoriaSeleccionada == categoria,
                    onClick = { viewModel.seleccionarCategoria(categoria) },
                    label = { Text(categoria) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Renderizado según fases de estado
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val fase = uiState.fase) {
                is FaseCatalogo.Cargando -> {
                    EstadoCarga(mensaje = "Cargando catálogo de la biblioteca...")
                }
                is FaseCatalogo.Error -> {
                    EstadoError(
                        mensaje = fase.mensaje,
                        onReintentar = { viewModel.cargarCatalogo() }
                    )
                }
                is FaseCatalogo.Vacio -> {
                    EstadoVacio(
                        titulo = "Sin libros encontrados",
                        subtitulo = "No hay libros que coincidan con los filtros o término de búsqueda."
                    )
                }
                is FaseCatalogo.Contenido -> {
                    // RF-02: Listas construidas obligatoriamente con LazyColumn
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(fase.libros, key = { it.id }) { libro ->
                            LibroItemCard(
                                libro = libro,
                                onClick = { onLibroClick(libro.id) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibroItemCard(
    libro: Libro,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tieneStock = libro.ejemplaresDisponibles > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${libro.autor} • ${libro.anio}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = libro.categoria,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Text(
                        text = "• ${libro.sede}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Badge de ejemplares disponibles
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (tieneStock) ColorDisponible.copy(alpha = 0.15f) else ColorAgotado.copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (tieneStock) "${libro.ejemplaresDisponibles} disp." else "Agotado",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (tieneStock) ColorDisponible else ColorAgotado,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
