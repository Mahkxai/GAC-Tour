package com.mahkxai.gactour.android.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.R

@Composable
fun GACTourTheme(
    // darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val fontProvider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    val fontName = GoogleFont("JetBrains Mono")

    val gacTourFontFamily = FontFamily(
        Font(googleFont = fontName, fontProvider = fontProvider),
        Font(googleFont = fontName, fontProvider = fontProvider, style = FontStyle.Italic),
        Font(googleFont = fontName, fontProvider = fontProvider, weight = FontWeight.Medium),
        Font(googleFont = fontName, fontProvider = fontProvider, style = FontStyle.Italic, weight = FontWeight.Medium),
        Font(googleFont = fontName, fontProvider = fontProvider, weight = FontWeight.Bold),
        Font(googleFont = fontName, fontProvider = fontProvider, style = FontStyle.Italic, weight = FontWeight.Bold),
    )

    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF03DAC5),
            tertiary = Color(0xFF3700B3)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC5),
            tertiary = Color(0xFF3700B3)
        )
    }

    val baseTextStyle = TextStyle(fontFamily = gacTourFontFamily)

    val gacTourTypography = Typography().run {
        copy(
            displayLarge = displayLarge.merge(baseTextStyle),
            displayMedium = displayMedium.merge(baseTextStyle),
            displaySmall = displaySmall.merge(baseTextStyle),
            headlineLarge = headlineLarge.merge(baseTextStyle),
            headlineMedium = headlineMedium.merge(baseTextStyle),
            headlineSmall = headlineSmall.merge(baseTextStyle),
            titleLarge = titleLarge.merge(baseTextStyle),
            titleMedium = titleMedium.merge(baseTextStyle),
            titleSmall = titleSmall.merge(baseTextStyle),
            bodyLarge = bodyLarge.merge(baseTextStyle),
            bodyMedium = bodyMedium.merge(baseTextStyle),
            bodySmall = bodySmall.merge(baseTextStyle),
            labelLarge = labelLarge.merge(baseTextStyle),
            labelMedium = labelMedium.merge(baseTextStyle),
            labelSmall = labelSmall.merge(baseTextStyle)
        )
    }

    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = gacTourTypography,
        shapes = shapes,
        content = content
    )
}
