package com.example.learnigapp


import androidx.compose.foundation.background
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

import androidx.compose.ui.graphics.Color

class Master {

    val tuerkies = Color(0xFF00A878)
    val green = Color(0xFF047807)
    val lightblue2 = Color(0xFFBAE4F7)

    @Composable
    fun App2() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "p2b") {
            composable("p2b") { P2b(navController) }
            composable("testing2") { Testing2(navController) }
        }
    }

    val appThemeList = listOf("Default","Darkmode", "Lightmode")

     lateinit var theme: Modifier
    @Composable
    fun appTheme(n:Int = 0):Modifier {

        val selectedTheme = appThemeList[n]

        theme = if (selectedTheme contentEquals "Default")
        { Modifier.fillMaxSize().background(lightblue2)}

        else if (selectedTheme contentEquals "Darkmode")
        { Modifier.fillMaxSize().background(Color.DarkGray)}

        else { Modifier.fillMaxSize().background(Color.White)}
        return this.theme
    }

    @Composable
    fun P2b(navController: NavController) {
        Column(modifier =  appTheme(0)) {
            Text("p2b")

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { navController.navigate("testing2") },
                modifier = Modifier.height(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = tuerkies))

            {
                Text("testing2")
            }
        }
    }

    @Composable
    fun Testing2(navController: NavController) {
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun App2Preview() {
        Master().App2()
}