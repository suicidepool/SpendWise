package com.oms.spendwise.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    primary = PrimaryBlueLight,
    onPrimary = Color.Black,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color.White,

    secondary = ChartBlue,
    onSecondary = Color.Black,

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE5E7EB),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE5E7EB),

    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),

    error = ExpenseRed,
    onError = Color.White,

    outline = Color(0xFF475569)
)


private val LightColorScheme = lightColorScheme(

    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = Color.White,

    secondary = ChartBlue,
    onSecondary = Color.White,

    background = BackgroundPrimary,
    onBackground = TextPrimary,

    surface = CardBackground,
    onSurface = TextPrimary,

    surfaceVariant = BackgroundElevated,
    onSurfaceVariant = TextSecondary,

    error = ExpenseRed,
    onError = Color.White,

    outline = DividerColor
)

@Composable
fun SpendWiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}