package pe.upeu.biblioandes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.KoinContext
import pe.upeu.biblioandes.presentation.navigation.AppNavHost
import pe.upeu.biblioandes.presentation.theme.BiblioAndesTheme

/**
 * Raíz composable multiplataforma de BiblioAndes (Android e iOS).
 * Envuelve la jerarquía en KoinContext y BiblioAndesTheme.
 */
@Composable
fun App() {
    KoinContext {
        BiblioAndesTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                AppNavHost()
            }
        }
    }
}
