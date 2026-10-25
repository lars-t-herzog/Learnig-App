package com.example.learnigapp

import com.example.learnigapp.ui.theme.Default
import com.example.learnigapp.ui.theme.DarkColors
import com.example.learnigapp.ui.theme.LightColors

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
import androidx.compose.ui.unit.sp

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.viewmodel.compose.viewModel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}



private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeSaves(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_mode")

    val themeMode: Flow<AppThemeMode> = context.dataStore.data.map { preferences ->
        AppThemeMode.entries.firstOrNull { it.name == preferences[themeKey] }
            ?: AppThemeMode.DEFAULT
    }

    suspend fun save(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = mode.name
        }
    }
}


enum class AppThemeMode(val label: String) {
    DEFAULT("Default"),
    DARKMODE("Darkmode"),
    LIGHTMODE("Lightmode")
}



@Composable
fun AppTheme(mode: AppThemeMode, content: @Composable () -> Unit) {
    val colorScheme = when (mode) {
        AppThemeMode.DEFAULT -> Default
        AppThemeMode.DARKMODE -> DarkColors
        AppThemeMode.LIGHTMODE -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}


class ThemeViewModel(application: Application) : AndroidViewModel(application) {


    private val saves = ThemeSaves(application)

    var appThemeMode by mutableStateOf(AppThemeMode.DEFAULT)
        private set

    init {
        viewModelScope.launch {
            appThemeMode = saves.themeMode.first()
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        appThemeMode = mode
        viewModelScope.launch { saves.save(mode) }
    }

    fun cycleThemeMode() {
        val modes = AppThemeMode.entries
        setThemeMode( modes[(appThemeMode.ordinal + 1) % modes.size])

    }
}



object Routes {
    const val HOME = "Home"
    const val SETTINGS = "Settings"
    const val ENGLISH = "English"
    const val GEOGRAPHY = "Geography"
    const val SECRET = "Secret"
}


@Composable
fun App(themeViewModel: ThemeViewModel = viewModel()) {
    val navController = rememberNavController()
    AppTheme(mode = themeViewModel.appThemeMode) {

        Scaffold { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(Routes.HOME) {
                    Home(
                        navController,
                        onCycleTheme = themeViewModel::cycleThemeMode
                    )
                }

                composable(Routes.SETTINGS) {
                    Settings(
                        navController,
                        currentMode = themeViewModel.appThemeMode,
                        onCycleTheme = themeViewModel::cycleThemeMode
                    )
                }

                composable(Routes.ENGLISH) {
                    English(
                        navController,
                        currentMode = themeViewModel.appThemeMode,
                        onCycleTheme = themeViewModel::cycleThemeMode
                    )
                }

                composable(Routes.GEOGRAPHY) {
                    Geography(
                        navController,
                        currentMode = themeViewModel.appThemeMode,
                        onCycleTheme = themeViewModel::cycleThemeMode
                    )
                }
                composable(Routes.SECRET) {
                    Secret(
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

var m: Int = 0

@Composable
fun Home(navController: NavController, onCycleTheme: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .padding(bottom = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button (onClick = { navController.navigate(Routes.SECRET) },colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background,contentColor = MaterialTheme.colorScheme.onBackground)) {Text(text = ("Home"), fontSize = 25.sp)}

        Button (onClick = { navController.navigate(Routes.SETTINGS) },
            modifier = Modifier.align(Alignment.End),
            contentPadding = PaddingValues(0.dp)
        )

        { Text(text = ("Settings"), fontSize = 20.sp) }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { Toast.makeText(context, "m = $m", Toast.LENGTH_SHORT).show() }) {
            Text(text = "print m")
        }


        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { m = (m + 1) }
        )

        {
            Text(text = ("Themes"), fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            Button(onClick = { navController.navigate(Routes.ENGLISH) },
                shape = RectangleShape,
                modifier = Modifier.width(210.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary))
            { Text(text = ("English"), fontSize = 20.sp) }
            Button(onClick = { navController.navigate(Routes.GEOGRAPHY)},
                shape = RectangleShape,
                modifier = Modifier.width(210.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
                )
            { Text(text = ("Geography"), fontSize = 20.sp) }
        }
    }
}

@Composable
fun Settings(
    navController: NavController,
    currentMode: AppThemeMode,
    onCycleTheme: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()                        // take the whole screen
            .padding(16.dp),                      // space inside the edge

        verticalArrangement = Arrangement.spacedBy(8.dp)  // gap between children
    ) {
        Text(text = ("Settings:"),fontSize = 24.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onCycleTheme) {
            Text("Theme: ${currentMode.label}")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),modifier = Modifier.align(Alignment.End)) {
            Button(onClick = { navController.popBackStack() }) { Text("Back") }
            Button(onClick = { navController.popBackStack() }) { Text("BBack") }
        }
    }

}

@Composable
fun English(navController: NavController,
              currentMode: AppThemeMode,
              onCycleTheme: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ){ Text(text = ("Vocabulary-Trainer:"),fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() },modifier = Modifier.align(Alignment.End)) { Text("Back") }}
}

@Composable
fun Geography(navController: NavController,
             currentMode: AppThemeMode,
             onCycleTheme: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ){  Text(text = ("World map knowledge quiz:"),fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() },modifier = Modifier.align(Alignment.End)) { Text("Back") }}
}
@Composable
fun Secret(navController: NavController,
           currentMode: AppThemeMode,
           onCycleTheme: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    )
    { Text("You`re not supposed to see this")
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() },modifier = Modifier.align(Alignment.End)) { Text("Back") }}
}

@Preview(showBackground = true)
@Composable
fun AppPreview() { App() }