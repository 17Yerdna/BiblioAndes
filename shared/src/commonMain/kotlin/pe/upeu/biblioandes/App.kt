package pe.upeu.biblioandes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            Text("BiblioAndes")
        }
    }
}
