package pe.upeu.biblioandes.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Controlador global de modo oscuro para conmutación inmediata en toda la app (RF-06)
class ThemeController(initialDarkMode: Boolean = false) {
    val isDarkMode = mutableStateOf(initialDarkMode)

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    fun setDarkMode(dark: Boolean) {
        isDarkMode.value = dark
    }
}

val LocalThemeController = compositionLocalOf { ThemeController() }

val BiblioAndesLightColorScheme: ColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = SuperficieLight,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = AzulPrimario,
    secondary = AzulSecundario,
    onSecondary = SuperficieLight,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = AzulSecundario,
    background = FondoLight,
    onBackground = TextoPrincipalLight,
    surface = SuperficieLight,
    onSurface = TextoPrincipalLight,
    surfaceVariant = SuperficieContainerLight,
    onSurfaceVariant = TextoSecundarioLight,
    surfaceContainer = SuperficieContainerLight
)

val BiblioAndesDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AzulPrimarioDark,
    onPrimary = FondoDark,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = AzulPrimarioDark,
    secondary = AzulSecundarioDark,
    onSecondary = FondoDark,
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = AzulSecundarioDark,
    background = FondoDark,
    onBackground = TextoPrincipalDark,
    surface = SuperficieDark,
    onSurface = TextoPrincipalDark,
    surfaceVariant = SuperficieContainerDark,
    onSurfaceVariant = TextoSecundarioDark,
    surfaceContainer = SuperficieContainerDark
)

@Composable
fun BiblioAndesTheme(
    controller: ThemeController = LocalThemeController.current,
    content: @Composable () -> Unit
) {
    val darkTheme = controller.isDarkMode.value
    val colorScheme = if (darkTheme) BiblioAndesDarkColorScheme else BiblioAndesLightColorScheme

    CompositionLocalProvider(LocalThemeController provides controller) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BiblioAndesTypography,
            content = content
        )
    }
}
