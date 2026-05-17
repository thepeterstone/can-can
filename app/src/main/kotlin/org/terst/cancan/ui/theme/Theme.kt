package org.terst.cancan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = TealLight,
    secondary = Amber700,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = AmberLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    secondary = AmberLight,
)

@Composable
fun CanCanTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
