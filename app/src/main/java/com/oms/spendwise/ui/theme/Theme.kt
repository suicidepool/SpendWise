package com.oms.spendwise.ui.theme

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
    onPrimary = TextOnPrimaryDark,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color(0xFFE5E7EB),

    secondary = ChartBlue,
    onSecondary = TextOnPrimaryDark,

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE5E7EB),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE9ECF1),

    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFC1CBD5),

    error = ExpenseRed,
    onError = Color(0xFFE5E7EB),

    outline = OutlineColor,
    outlineVariant = DividerColorDark
)


private val LightColorScheme = lightColorScheme(

    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextOnPrimary,

    secondary = ChartBlue,
    onSecondary = TextOnPrimary,

    background = BackgroundPrimary,
    onBackground = TextPrimary,

    surface = CardBackground,
    onSurface = TextPrimary,

    surfaceVariant = BackgroundElevated,
    onSurfaceVariant = TextSecondary,

    error = AlertRed,
    onError = TextOnPrimary,

    outline = OutlineColor,
    outlineVariant = DividerColor
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