package com.example.learnigapp


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewmodel.compose.viewModel



enum class AppThemeMode(val label:  String) {
    DEFAULT("Default"),
    DARKMODE("Darkmode"),
    LIGHTMODE("Lightmode") }

private val Default = lightColorScheme(
    primary = Color(0xFF047807),        //background of Botton
    onPrimary = Color(0xFF000000),      //text of Botton
    background = Color(0xFFBAE4F7),     //background of App
    onBackground = Color(0xFF0037ED),   //text on background
    surface = Color(0xFF00A878),        //containers on background
    onSurface = Color(0xFF000000))      //color of text in containers

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3D3D3D),//background of Botton
    onPrimary = Color(0xFFFFFFFF),//text of Botton
    background = Color(0xFF000000),//background of App
    onBackground = Color(0xFFFFFFFF),//text on background
    surface = Color(0xFF636363),//containers on background
    onSurface = Color(0xFFFFFFFF))//color of text in containers

private val LightColors = lightColorScheme(
    primary = Color(0xFFBFBFBF),//background of Botton
    onPrimary = Color(0xFF000000),//text of Botton
    background = Color(0xFFFFFFFF),//background of App
    onBackground = Color(0xFF000000),//text on background
    surface = Color(0xFF9C9C9C),//containers on background
    onSurface = Color(0xFF000000))//color of text in containers

@Composable
fun AppTheme(mode: AppThemeMode, content: @Composable () -> Unit) {
    val colorScheme = when (mode) {
        AppThemeMode.DEFAULT -> Default
        AppThemeMode.DARKMODE -> DarkColors
        AppThemeMode.LIGHTMODE -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}


class ThemeViewModel : ViewModel() {
    var appThemeMode by mutableStateOf(AppThemeMode.DEFAULT)
        private set
    fun setThemeMode(mode: AppThemeMode){
        appThemeMode = mode
    }

    fun cycleThemeMode() {
        val modes = AppThemeMode.entries
        appThemeMode = modes[(appThemeMode.ordinal + 1) % modes.size]

    }
}

    //val tuerkies = Color(0xFF00A878)
    val green = Color(0xFF047807)
    // val lightblue2 = Color(0xFFBAE4F7)

    object Routes {
        const val P2B = "P2b"
        const val TESTING2 = "Testing2"
    }


    @Composable
    fun App2(themeViewModel: ThemeViewModel = viewModel()) {
        val navController = rememberNavController()
        AppTheme(mode = themeViewModel.appThemeMode) {

            Scaffold { innerPadding ->

                NavHost(
                    navController = navController,
                    startDestination = Routes.P2B,
                    modifier = Modifier.padding(innerPadding)
                ) {

                    composable(Routes.P2B) {
                        P2b(
                            navController,
                            onCycleTheme = themeViewModel::cycleThemeMode
                        )
                    }

                    composable(Routes.TESTING2) {
                        Testing2(
                            navController,
                            currentMode = themeViewModel.appThemeMode,
                            onCycleTheme = themeViewModel::cycleThemeMode
                        )
                    }
                }
            }
        }
    }

        //   val appThemeList = listOf("Default","Darkmode", "Lightmode")

        //  lateinit var theme: Modifier
        //   @Composable
        //  fun appTheme(m: Int):Modifier {
        //      val selectedTheme = appThemeList[m]
//
        //       theme = if (selectedTheme contentEquals "Default")
        //      { Modifier.fillMaxSize().background(lightblue2)}
//
        //       else if (selectedTheme contentEquals "Darkmode")
        //       { Modifier.fillMaxSize().background(Color.DarkGray)}
//
        //       else { Modifier.fillMaxSize().background(Color.White)}
        //       return this.theme
        //   }

        var m:Int = 0
        @Composable
        fun P2b(navController: NavController, onCycleTheme: () -> Unit ) {
            val context = LocalContext.current
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = ("p2b"), fontSize = 25.sp)

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { navController.navigate(Routes.TESTING2) },
                    modifier = Modifier.height(40.dp).width(130.dp),
                )

                {
                    Text(text = ("Testing2"), fontSize = 20.sp)
                }

                Button(onClick = { Toast.makeText(context, "m = $m", Toast.LENGTH_SHORT).show() }) {
                    Text(text = "print m")
                }


                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { m = (m + 1) },
                    modifier = Modifier.height(40.dp).width(130.dp),
                )

                {
                    Text(text = ("Themes"), fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        @Composable
        fun Testing2(navController: NavController, currentMode: AppThemeMode, onCycleTheme: () -> Unit) {
            Column(
                modifier = Modifier
                    .fillMaxSize()                        // take the whole screen
                    .padding(16.dp),                      // space inside the edge

                verticalArrangement = Arrangement.spacedBy(8.dp)  // gap between children
            ) {
                Text("lets see")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { navController.popBackStack() }) { Text("A") }
                    Button(onClick = { navController.popBackStack() }) { Text("B") }
                }

                Button(onClick = onCycleTheme) {
                    Text("Theme: ${currentMode.label}")
                }
            }
        }


@Preview(showBackground = true)
@Composable
fun App2Preview() {
        App2()
}