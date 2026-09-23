package pe.upeu.biblioandes.presentation.prestamos

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoError
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.theme.ColorActivo
import pe.upeu.biblioandes.presentation.theme.ColorDevuelto
import pe.upeu.biblioandes.presentation.theme.ColorVencido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamosScreen(
    viewModel: PrestamosViewModel,
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
            text = "Mis Préstamos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Controla tus plazos y fechas de devolución",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // RF-04: Filtro por estado del préstamo (Chips)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroEstadoPrestamo.entries.forEach { filtro ->
                FilterChip(
                    selected = uiState.filtroSeleccionado == filtro,
                    onClick = { viewModel.cambiarFiltro(filtro) },
                    label = { Text(filtro.etiqueta) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Estados de interfaz
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val fase = uiState.fase) {
                is FasePrestamos.Cargando -> {
                    EstadoCarga(mensaje = "Cargando tus préstamos...")
                }
                is FasePrestamos.Error -> {
                    EstadoError(
                        mensaje = fase.mensaje,
                        onReintentar = { viewModel.cargarPrestamos() }
                    )
                }
                is FasePrestamos.Vacio -> {
                    EstadoVacio(
                        titulo = "No hay préstamos registrados",
                        subtitulo = "No se encontraron préstamos para la categoría seleccionada.",
                        icono = Icons.AutoMirrored.Filled.Assignment
                    )
                }
                is FasePrestamos.Contenido -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(fase.prestamos, key = { it.id }) { prestamo ->
                            PrestamoItemCard(
                                prestamo = prestamo,
                                onClick = { onLibroClick(prestamo.libro.id) }
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
fun PrestamoItemCard(
    prestamo: Prestamo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorEstado, textoEstado, iconoEstado) = when (val estado = prestamo.estado) {
        is EstadoPrestamo.Activo -> Triple(
            ColorActivo,
            if (estado.diasRestantes == 1) "Vence mañana" else "Quedan ${estado.diasRestantes} días",
            Icons.Default.Schedule
        )
        is EstadoPrestamo.Devuelto -> Triple(
            ColorDevuelto,
            "Devuelto (${estado.fechaDevolucion})",
            Icons.Default.CheckCircle
        )
        is EstadoPrestamo.Vencido -> Triple(
            ColorVencido,
            "Vencido hace ${estado.diasDeAtraso} días",
            Icons.Default.Error
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = colorEstado.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = iconoEstado,
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = textoEstado,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colorEstado
                        )
                    }
                }

                Text(
                    text = "ID: #${prestamo.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = prestamo.libro.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Autor: ${prestamo.libro.autor} • Sede: ${prestamo.libro.sede}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fecha de préstamo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = prestamo.fechaPrestamo,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Fecha límite (RN-03)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = prestamo.fechaLimite,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (prestamo.estado is EstadoPrestamo.Vencido) ColorVencido else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
