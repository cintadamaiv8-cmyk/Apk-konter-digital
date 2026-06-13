package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CosmicDarkColorScheme = darkColorScheme(
    primary = CosmicPurple,
    secondary = GalaxyBlue,
    tertiary = NebulaCyan,
    background = VoidBlack,
    surface = SurfaceDark,
    onPrimary = StarlightSilver,
    onSecondary = StarlightSilver,
    onTertiary = VoidBlack,
    onBackground = StarlightSilver,
    onSurface = StarlightSilver,
    error = DangerRed,
    onError = StarlightSilver
)

@Composable
fun MyApplicationTheme(
    // Enforce dark theme
    darkTheme: Boolean = true,
    // Disable dynamic color to maintain cosmic theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = CosmicDarkColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
