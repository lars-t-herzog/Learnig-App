package com.example.learnigapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnigapp.ui.theme.LearnigAppTheme



@Composable
fun MyApp() {
    var backgroundIndex by remember { mutableIntStateOf(0) }
    var title by remember { mutableStateOf("Lernapp Lars") }
    var showSquare by remember { mutableStateOf(true) }

    val colors = listOf(
        Color(0xFFDDE9F8),
        Color(0xFFFFE0E0),
        Color(0xFFDDF5E1),
        Color(0xFFFFF0C9),
        Color(0xFFE8DDF8)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors[backgroundIndex])
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Editable text field at the top
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("App title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Change background color
        OutlinedButton(
            onClick = {
                backgroundIndex =
                    (backgroundIndex + 1) % colors.size
            }
        ) {
            Text("Change background color")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Square appears after pressing the button
        if (showSquare) {
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        2.dp,
                        Color.DarkGray,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hello, world!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Big button at the bottom
        Button(
            onClick = { showSquare = !showSquare },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Neue Vokabel",
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    LearnigAppTheme {
        MyApp()
    }
}