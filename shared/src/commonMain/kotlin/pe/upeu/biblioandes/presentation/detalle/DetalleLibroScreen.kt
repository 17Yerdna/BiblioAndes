package pe.upeu.biblioandes.presentation.detalle

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.theme.ColorAgotado
import pe.upeu.biblioandes.presentation.theme.ColorDisponible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLibroScreen(
    libroId: Int,
    viewModel: DetalleLibroViewModel,
    onVolverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(libroId) {
        viewModel.cargarDetalle(libroId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Libro") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.cargando -> {
                    EstadoCarga(mensaje = "Cargando detalles del libro...")
                }
                uiState.libro != null -> {
                    val libro = uiState.libro!!
                    val tieneStock = libro.ejemplaresDisponibles > 0

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tarjeta principal con icono y título
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = libro.titulo,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = libro.autor,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // RF-03: Ficha técnica del libro (Año, Categoría, Sede, Ejemplares)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Ficha Bibliográfica",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                FilaDetalle(
                                    icono = Icons.Default.Person,
                                    etiqueta = "Autor",
                                    valor = libro.autor
                                )
                                FilaDetalle(
                                    icono = Icons.Default.CalendarMonth,
                                    etiqueta = "Año de publicación",
                                    valor = "${libro.anio}"
                                )
                                FilaDetalle(
                                    icono = Icons.Default.Category,
                                    etiqueta = "Categoría",
                                    valor = libro.categoria
                                )
                                FilaDetalle(
                                    icono = Icons.Default.LocationOn,
                                    etiqueta = "Sede bibliotecaria",
                                    valor = libro.sede
                                )
                                FilaDetalle(
                                    icono = Icons.Default.LibraryBooks,
                                    etiqueta = "Ejemplares en estante",
                                    valor = if (tieneStock) "${libro.ejemplaresDisponibles} copias disponibles" else "Agotado",
                                    valorColor = if (tieneStock) ColorDisponible else ColorAgotado
                                )
                            }
                        }

                        // Alerta de error si ocurrió alguna violación de regla de negocio
                        if (uiState.error != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = uiState.error!!,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }

                        // Alerta de confirmación de préstamo exitoso
                        if (uiState.prestamoExitoso != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = ColorDisponible.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ColorDisponible
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "¡Préstamo registrado con éxito!",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorDisponible
                                        )
                                        Text(
                                            text = "Fecha límite de devolución: ${uiState.prestamoExitoso!!.fechaLimite} (7 días)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f, fill = false))

                        // RF-03: Botón «Solicitar préstamo»
                        Button(
                            onClick = { viewModel.abrirDialogoConfirmacion() },
                            enabled = tieneStock && !uiState.solicitando,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (uiState.solicitando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Procesando solicitud...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.LibraryBooks,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (tieneStock) "Solicitar préstamo" else "Sin ejemplares disponibles"
                                )
                            }
                        }
                    }

                    // RF-03: Diálogo modal de confirmación
                    if (uiState.mostrarDialogoConfirmacion) {
                        AlertDialog(
                            onDismissRequest = { viewModel.cerrarDialogoConfirmacion() },
                            title = {
                                Text(
                                    text = "Confirmar Solicitud de Préstamo",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("¿Deseas solicitar en préstamo el siguiente título?")
                                    Text(
                                        text = "«${libro.titulo}»",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "• Duración: 7 días calendario (RN-03)\n• Sede de recojo: ${libro.sede}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { viewModel.confirmarSolicitudPrestamo() }
                                ) {
                                    Text("Confirmar")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { viewModel.cerrarDialogoConfirmacion() }
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDetalle(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    valorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(140.dp)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valorColor
        )
    }
}
