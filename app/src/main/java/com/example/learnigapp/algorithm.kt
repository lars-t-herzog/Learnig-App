package com.example.learnigapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlgorithmButton(onOpenAlgorithm: () -> Unit) {
    Button(onClick = onOpenAlgorithm) {
        Text("Algorithm")
    }
}

class AlgorithmClass {
    var n: Int = 0
    var g: Boolean = true

    fun algorithm() {
        when (n) {
            0 -> n += if (g) 2 else 1
            1 -> n += if (g) 1 else 0
            2 -> n += if (g) 1 else -1
            3 -> n += if (g) 1 else -1
            4 -> n += if (g) 1 else -1
            5 -> n += if (g) 1 else -1
            6 -> n += if (g) 1 else -1
            7 -> println("your done!")
        }
    }
}

class AlgorithmTestClass {
    var m: Int = 0
    var p: Boolean = true
    var lastLog: String = ""

    fun algorithm(): String {
        val initialM = m
        val initialP = p

        when (m) {
            0 -> m += if (p) 2 else 1
            1 -> m += if (p) 1 else 0
            2 -> m += if (p) 1 else -1
            3 -> m += if (p) 1 else -1
            4 -> m += if (p) 1 else -1
            5 -> m += if (p) 1 else -1
            6 -> m += if (p) 1 else -1
        }

        val log = if (initialM != 7) {
            "Tested: m was $initialM, p was $initialP -> m is now $m"
        } else {
            "your done!"
        }

        lastLog = log
        return log
    }
}

@Composable
fun AlgorithmScreen() {
    var mState by remember { mutableIntStateOf(0) }
    var pState by remember { mutableStateOf(true) }
    var logText by remember { mutableStateOf("No test run yet") }

    val algo = remember { AlgorithmTestClass() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Current m: $mState", fontSize = 28.sp)
        Text(text = "Current p: $pState", fontSize = 20.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { mState += 1 }) {
                Text("Increase m (+1)")
            }

            Button(onClick = { pState = !pState }) {
                Text("Toggle p (${if (pState) "true" else "false"})")
            }
        }

        Button(onClick = {
            algo.m = mState
            algo.p = pState
            logText = algo.algorithm()
            mState = algo.m
        }) {
            Text("Run Algorithm")
        }

        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Test Log:", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = logText, fontSize = 18.sp)
            }
        }

        Button(onClick = { mState = 0; pState = true }) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlgorithmPreview() {
    AlgorithmScreen()
}