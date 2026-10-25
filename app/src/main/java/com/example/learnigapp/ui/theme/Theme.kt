package com.example.learnigapp.ui.theme

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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)




            public val Default = lightColorScheme(
        primary = Color(0xFF047807),        //background of Botton
        onPrimary = Color(0xFF000000),      //text of Botton
        background = Color(0xFFBAE4F7),     //background of App
        onBackground = Color(0xFF0037ED),   //text on background
        surface = Color(0xFF00A878),        //containers on background
        onSurface = Color(0xFF000000)       //color of text in containers
    )

            public val DarkColors = darkColorScheme(
        primary = Color(0xFF3D3D3D),//background of Botton
        onPrimary = Color(0xFFFFFFFF),//text of Botton
        background = Color(0xFF000000),//background of App
        onBackground = Color(0xFFFFFFFF),//text on background
        surface = Color(0xFF636363),//containers on background
        onSurface = Color(0xFFFFFFFF)//color of text in containers
    )

            public val LightColors = lightColorScheme(
        primary = Color(0xFFBFBFBF),//background of Botton
        onPrimary = Color(0xFF000000),//text of Botton
        background = Color(0xFFFFFFFF),//background of App
        onBackground = Color(0xFF000000),//text on background
        surface = Color(0xFF9C9C9C),//containers on background
        onSurface = Color(0xFF000000)//color of text in containers
    )

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun LearnigAppTheme(
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