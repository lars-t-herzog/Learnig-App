package com.example.learnigapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AlgorithmButton(onOpenAlgorithm: () -> Unit) {
    Button(onClick = onOpenAlgorithm) {
        Text("Algorithm")
    }
}

/**
 * Spaced Repetition Spacing Algorithm:
 * Calculates next repetitions level, intervalDays, and dueDay based on user response (knewIt).
 */
fun calculateNextReview(vocab: Vocab, knewIt: Boolean): Vocab {
    val today = System.currentTimeMillis() / 86_400_000L
    var nextRepetitions: Int
    var nextIntervalDays: Int

    if (knewIt) {
        // User knew the word ("I knew") -> advance repetition level
        when (vocab.repetitions) {
            0 -> { nextRepetitions = 2; nextIntervalDays = 1 }
            1 -> { nextRepetitions = 2; nextIntervalDays = 1 }
            2 -> { nextRepetitions = 3; nextIntervalDays = 2 }
            3 -> { nextRepetitions = 4; nextIntervalDays = 3 }
            4 -> { nextRepetitions = 5; nextIntervalDays = 7 }
            5 -> { nextRepetitions = 6; nextIntervalDays = 14 }
            6 -> { nextRepetitions = 7; nextIntervalDays = 0 } // Marked as learned
            else -> { nextRepetitions = 7; nextIntervalDays = 0 } // Already learned
        }
    } else {
        // User made a mistake ("I didn't know") -> step back repetition level
        when (vocab.repetitions) {
            0 -> { nextRepetitions = 1; nextIntervalDays = 1 }
            1 -> { nextRepetitions = 1; nextIntervalDays = 1 }
            2 -> { nextRepetitions = 1; nextIntervalDays = 1 }
            3 -> { nextRepetitions = 2; nextIntervalDays = 1 }
            4 -> { nextRepetitions = 3; nextIntervalDays = 2 }
            5 -> { nextRepetitions = 4; nextIntervalDays = 3 }
            6 -> { nextRepetitions = 5; nextIntervalDays = 7 }
            else -> { nextRepetitions = 5; nextIntervalDays = 7 } // Drop back down from learned to revise
        }
    }

    return vocab.copy(
        repetitions = nextRepetitions,
        intervalDays = nextIntervalDays,
        dueDay = today + nextIntervalDays
    )
}

class AlgorithmClass {
    var repetitions: Int = 0
    var p: Boolean = true

    fun algorithm() {
        when (repetitions) {
            0 -> repetitions += if (p) 2 else 1
            1 -> repetitions += if (p) 1 else 0
            2 -> repetitions += if (p) 1 else -1
            3 -> repetitions += if (p) 1 else -1
            4 -> repetitions += if (p) 1 else -1
            5 -> repetitions += if (p) 1 else -1
            6 -> repetitions += if (p) 1 else -1
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

        val log = if (initialM == 7) {
            "your done!"
        } else {
            when (m) {
                0 -> m += if (p) 2 else 1
                1 -> m += if (p) 1 else 0
                2 -> m += if (p) 1 else -1
                3 -> m += if (p) 1 else -1
                4 -> m += if (p) 1 else -1
                5 -> m += if (p) 1 else -1
                6 -> m += if (p) 1 else -1
            }
            "Tested: m was $initialM, p was $initialP -> m is now $m"
        }

        lastLog = log
        return log
    }
}

/**
 * VocabManagerCard: The UI card for Adding and Deleting Vocabulary in Room SQLite.
 * Supports case-insensitive searching & matching on BOTH Word and Translation!
 */
@Composable
fun VocabManagerCard(vocabViewModel: VocabViewModel = viewModel()) {
    var wordInput by remember { mutableStateOf("") }
    var translationInput by remember { mutableStateOf("") }
    var isAddMode by remember { mutableStateOf(true) } // true = Add Mode (default), false = Delete Mode
    var statusMessage by remember { mutableStateOf("") }

    val allVocabs by vocabViewModel.allVocab.collectAsState()

    // Case-insensitive two-way search matching on BOTH Word AND Translation
    val matchingWords = remember(wordInput, allVocabs.size) {
        val matches = mutableListOf<Vocab>()
        val query = wordInput.trim().lowercase()
        if (query.isNotEmpty()) {
            for (vocab in allVocabs) {
                val matchWord = vocab.word.lowercase().contains(query)
                val matchTrans = vocab.translation.lowercase().contains(query)
                if (matchWord || matchTrans) {
                    matches.add(vocab)
                }
            }
            matches.sortBy { it.word.lowercase() }
        }
        matches.take(10)
    }

    // Gray foreshadow suggestion for autocomplete (case-insensitive)
    val foreshadowSuggestion = remember(wordInput, matchingWords) {
        val query = wordInput.trim().lowercase()
        if (matchingWords.isNotEmpty() && query.isNotEmpty()) {
            val topMatch = matchingWords[0]
            if (topMatch.word.lowercase().startsWith(query)) {
                topMatch.word
            } else if (topMatch.translation.lowercase().startsWith(query)) {
                topMatch.translation
            } else {
                ""
            }
        } else {
            ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Add / Delete Vocabulary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Mode Selection: 2 Small Radio Buttons (OR Condition)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Mode:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isAddMode = true }
                ) {
                    RadioButton(
                        selected = isAddMode,
                        onClick = { isAddMode = true },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isAddMode = false }
                ) {
                    RadioButton(
                        selected = !isAddMode,
                        onClick = { isAddMode = false },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Delete", fontSize = 14.sp)
                }
            }

            // Word TextField with Gray Foreshadowing (Both-way case-insensitive)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = wordInput,
                    onValueChange = { wordInput = it },
                    label = { Text("Word/Translation to ${if (isAddMode) "Add" else "Delete"}") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (foreshadowSuggestion.isNotEmpty() && foreshadowSuggestion.lowercase() != wordInput.trim().lowercase()) {
                    Text(
                        text = foreshadowSuggestion,
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp, top = 6.dp)
                    )
                }
            }

            // Translation TextField (Add mode only)
            if (isAddMode) {
                OutlinedTextField(
                    value = translationInput,
                    onValueChange = { translationInput = it },
                    label = { Text("Translation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Alphabetical Suggestions List (Searches Word & Translation)
            if (matchingWords.isNotEmpty()) {
                Text(
                    text = "Matches in Database (Word / Translation):",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (item in matchingWords) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    wordInput = item.word
                                    translationInput = item.translation
                                }
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.word, fontWeight = FontWeight.Medium)
                            Text(text = item.translation, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Action Button
            Button(
                onClick = {
                    val inputTarget = wordInput.trim()
                    if (inputTarget.isNotBlank()) {
                        if (isAddMode) {
                            // Insert into Room SQLite database with repetitions = 0
                            vocabViewModel.insertVocab(inputTarget, translationInput.trim())
                            statusMessage = "Added to Database: '$inputTarget'"
                        } else {
                            // Case-insensitive deletion matching on BOTH Word OR Translation
                            val match = allVocabs.firstOrNull {
                                it.word.equals(inputTarget, ignoreCase = true) ||
                                it.translation.equals(inputTarget, ignoreCase = true)
                            }
                            if (match != null) {
                                vocabViewModel.deleteVocab(match)
                                statusMessage = "Deleted from Database: '${match.word}' (${match.translation})"
                            } else {
                                statusMessage = "Word/Translation '$inputTarget' not found"
                            }
                        }
                        wordInput = ""
                        translationInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAddMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isAddMode) "Add Vocabulary" else "Delete Vocabulary")
            }

            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AlgorithmScreen(navController: NavController) {
    var repetitionsState by remember { mutableIntStateOf(0) }
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
        Text(text = "Current repetitions: $repetitionsState", fontSize = 28.sp)
        Text(text = "Current p: $pState", fontSize = 20.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { repetitionsState += 1 }) {
                Text("Increase repetitions (+1)")
            }

            Button(onClick = { pState = !pState }) {
                Text("Toggle p (${if (pState) "true" else "false"})")
            }
        }

        Button(onClick = {
            algo.m = repetitionsState
            algo.p = pState
            logText = algo.algorithm()
            repetitionsState = algo.m
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

        Button(onClick = { repetitionsState = 0; pState = true }) {
            Text("Reset")
        }

        Button(
            onClick = { navController.popBackStack(Routes.HOME, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }
}

@Preview(showBackground = true)
@Composable
fun AlgorithmPreview() {
    val navController = rememberNavController()
    AlgorithmScreen(navController = navController)
}
