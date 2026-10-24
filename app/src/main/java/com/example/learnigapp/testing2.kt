package com.example.learnigapp


import androidx.compose.foundation.background
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


//enum class appThemeMode(val label:  String) {
   // DEFAULT("Default"),
    //DARKMODE("Darkmode"),
    //LIGHTMODE("Lightmode") }

//private val DefaultColors = lightColorScheme(
 //   primary = Color(0xFF00A878),onPrimary = Color(0xFF0037ED),background = Color(0xFFBAE4F7),onBackground = Color(0xFFED8700),surface = Color(0xFF047807),onSurface = Color(0xFFDD00ED))


class Master(var m: Int = 0) {

    //val tuerkies = Color(0xFF00A878)
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
    fun appTheme(m: Int):Modifier {
        val selectedTheme = appThemeList[m]

        theme = if (selectedTheme contentEquals "Default")
        { Modifier.fillMaxSize().background(lightblue2)}

        else if (selectedTheme contentEquals "Darkmode")
        { Modifier.fillMaxSize().background(Color.DarkGray)}

        else { Modifier.fillMaxSize().background(Color.White)}
        return this.theme
    }




    @Composable
    fun P2b(navController: NavController) {
        val context = LocalContext.current
        Column(modifier =  appTheme(m),
                horizontalAlignment = Alignment.CenterHorizontally) {
            Text( text = ("p2b"),fontSize = 25.sp)

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { navController.navigate("testing2") },
                modifier = Modifier.height(40.dp).width(130.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black))

            {
                Text( text = ("Testing2"),fontSize = 20.sp)
            }

            Button(onClick = { Toast.makeText(context,"m = $m", Toast.LENGTH_SHORT).show()}) {
                Text(text = "print m")
            }


            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { m = (m+1) },
                modifier = Modifier.height(40.dp).width(130.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black))

            {
                Text( text = ("Themes"), fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
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