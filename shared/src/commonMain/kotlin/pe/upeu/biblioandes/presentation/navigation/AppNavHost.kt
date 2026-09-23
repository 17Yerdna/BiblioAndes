package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.presentation.catalogo.CatalogoScreen
import pe.upeu.biblioandes.presentation.catalogo.CatalogoViewModel
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroScreen
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroViewModel
import pe.upeu.biblioandes.presentation.inicio.InicioScreen
import pe.upeu.biblioandes.presentation.inicio.InicioViewModel
import pe.upeu.biblioandes.presentation.perfil.PerfilScreen
import pe.upeu.biblioandes.presentation.prestamos.PrestamosScreen
import pe.upeu.biblioandes.presentation.prestamos.PrestamosViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    var destinoActual by rememberSaveable(stateSaver = DestinoSaver) {
        mutableStateOf(Destino.Inicio)
    }

    // Historial para gestión de botón atrás
    val historialDestinos = remember { mutableStateListOf<Destino>() }

    fun navegarA(nuevoDestino: Destino) {
        if (nuevoDestino != destinoActual) {
            historialDestinos.add(destinoActual)
            destinoActual = nuevoDestino
        }
    }

    fun volverAtras() {
        if (historialDestinos.isNotEmpty()) {
            val destinoAnterior = historialDestinos.removeAt(historialDestinos.lastIndex)
            destinoActual = destinoAnterior
        } else if (destinoActual != Destino.Inicio) {
            destinoActual = Destino.Inicio
        }
    }

    val esPantallaDetalle = destinoActual is Destino.Detalle

    Scaffold(
        bottomBar = {
            // RF-07: Barra de navegación inferior visible en destinos principales
            if (!esPantallaDetalle) {
                NavigationBar {
                    Destino.DESTINOS_BARRA_INFERIOR.forEach { destino ->
                        val seleccionado = destinoActual == destino
                        NavigationBarItem(
                            selected = seleccionado,
                            onClick = {
                                if (destinoActual != destino) {
                                    navegarA(destino)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destino.icono,
                                    contentDescription = destino.titulo
                                )
                            },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val destino = destinoActual) {
                is Destino.Inicio -> {
                    val inicioViewModel: InicioViewModel = koinViewModel()
                    InicioScreen(
                        viewModel = inicioViewModel,
                        onIrAlCatalogo = { navegarA(Destino.Catalogo) },
                        onIrAMisPrestamos = { navegarA(Destino.Prestamos) },
                        onIrAlDetalleLibro = { libroId -> navegarA(Destino.Detalle(libroId)) }
                    )
                }

                is Destino.Catalogo -> {
                    val catalogoViewModel: CatalogoViewModel = koinViewModel()
                    CatalogoScreen(
                        viewModel = catalogoViewModel,
                        onLibroClick = { libroId -> navegarA(Destino.Detalle(libroId)) }
                    )
                }

                is Destino.Prestamos -> {
                    val prestamosViewModel: PrestamosViewModel = koinViewModel()
                    PrestamosScreen(
                        viewModel = prestamosViewModel,
                        onLibroClick = { libroId -> navegarA(Destino.Detalle(libroId)) }
                    )
                }

                is Destino.Perfil -> {
                    val repository: BibliotecaRepository = koinInject()
                    PerfilScreen(
                        repository = repository
                    )
                }

                is Destino.Detalle -> {
                    val detalleViewModel: DetalleLibroViewModel = koinViewModel()
                    DetalleLibroScreen(
                        libroId = destino.libroId,
                        viewModel = detalleViewModel,
                        onVolverClick = { volverAtras() }
                    )
                }
            }
        }
    }
}
