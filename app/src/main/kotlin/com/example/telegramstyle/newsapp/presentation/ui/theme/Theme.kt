package com.example.telegramstyle.newsapp.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// HyperOS Dark Theme Colors
private val HyperOSDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4A90D9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A3A5C),
    onPrimaryContainer = Color(0xFFD1E4FF),
    
    secondary = Color(0xFF6C63FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2D2A5C),
    onSecondaryContainer = Color(0xFFE0DEFF),
    
    tertiary = Color(0xFF4CAF50),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1F3D20),
    onTertiaryContainer = Color(0xFFC8E6C9),
    
    error = Color(0xFFEF5350),
    onError = Color.White,
    errorContainer = Color(0xFF2D1F1F),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF0D0D0D),
    onBackground = Color.White,
    
    surface = Color(0xFF1A1A1A),
    onSurface = Color.White,
    
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB3B3B3),
    
    outline = Color(0xFF333333),
    outlineVariant = Color(0xFF444444),
    
    inverseSurface = Color(0xFFE6E6E6),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = Color(0xFF3A7BC8)
)

// HyperOS Light Theme Colors
private val HyperOSLightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90D9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    
    secondary = Color(0xFF6C63FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0DEFF),
    onSecondaryContainer = Color(0xFF1A1852),
    
    tertiary = Color(0xFF4CAF50),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF0D260E),
    
    error = Color(0xFFEF5350),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),
    
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFCCCCCC),
    
    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFFE6E6E6),
    inversePrimary = Color(0xFF8FBFFF)
)

@Composable
fun TelegramStyleNewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> HyperOSDarkColorScheme
        else -> HyperOSLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
