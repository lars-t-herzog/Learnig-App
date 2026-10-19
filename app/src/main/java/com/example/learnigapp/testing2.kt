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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue


@Composable
fun App2() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "p2b") {
        composable("p2b") { P2b(navController) }
        composable("testing2") { Testing2(navController) }
    }
}
@Composable
fun P2b(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("p2b")
        Button(onClick = { navController.navigate("testing2") }) {
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
            .background(Color.Blue),        // background color
        verticalArrangement = Arrangement.spacedBy(8.dp),   // gap between children
    ) {
        Text("lets see")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.popBackStack() }) { Text("A") }
            Button(onClick = { navController.popBackStack() }) { Text("B") }
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