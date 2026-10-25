package com.example.learnigapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learnigapp.ui.theme.LearnigAppTheme

@Composable
fun App2() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController) }
        composable("settings") { Settings(navController) }
        composable("geographie") { Geographie(navController) }
    }
}

@Composable
fun Home(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("home")
        Button(onClick = { navController.navigate("settings") }) {
            Text("settings")
        }
        Button(onClick = { navController.navigate("geographie") }) {
            Text("Geographie")
        }
    }

}

@Composable
fun Settings(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("settings")
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

@Composable
fun Geographie(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Geographie")
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun App2Preview() {
    LearnigAppTheme {
        App2()
    }
}
