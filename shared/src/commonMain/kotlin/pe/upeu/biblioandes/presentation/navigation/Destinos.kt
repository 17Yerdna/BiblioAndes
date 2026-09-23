package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Destinos de navegación tipados para BiblioAndes (RF-07).
 */
sealed class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    data object Inicio : Destino("inicio", "Inicio", Icons.Default.Home)
    data object Catalogo : Destino("catalogo", "Catálogo", Icons.Default.Book)
    data object Prestamos : Destino("prestamos", "Préstamos", Icons.AutoMirrored.Filled.Assignment)
    data object Perfil : Destino("perfil", "Perfil", Icons.Default.Person)
    data class Detalle(val libroId: Int) : Destino("detalle/$libroId", "Detalle", Icons.Default.Book)

    companion object {
        val DESTINOS_BARRA_INFERIOR: List<Destino> = listOf(Inicio, Catalogo, Prestamos, Perfil)

        fun desdeRuta(ruta: String?): Destino {
            if (ruta == null) return Inicio
            return when {
                ruta == Inicio.ruta -> Inicio
                ruta == Catalogo.ruta -> Catalogo
                ruta == Prestamos.ruta -> Prestamos
                ruta == Perfil.ruta -> Perfil
                ruta.startsWith("detalle/") -> {
                    val id = ruta.substringAfter("detalle/").toIntOrNull() ?: 1
                    Detalle(id)
                }
                else -> Inicio
            }
        }
    }
}

val DestinoSaver: Saver<Destino, String> = Saver(
    save = { it.ruta },
    restore = { Destino.desdeRuta(it) }
)
