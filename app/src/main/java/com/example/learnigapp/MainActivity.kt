package com.example.learnigapp

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learnigapp.ui.theme.DarkColors
import com.example.learnigapp.ui.theme.Default
import com.example.learnigapp.ui.theme.LightColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

class Sounds(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var clickId: Int = 0

    init {
        try {
            clickId = soundPool.load(context, R.raw.cow_mooh, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playClick() {
        if (clickId != 0) {
            soundPool.play(clickId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
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
        setThemeMode(modes[(appThemeMode.ordinal + 1) % modes.size])
    }
}

object Routes {
    const val HOME = "Home_Page"
    const val SETTINGS = "Settings_Page"
    const val ENGLISH = "English_Page"
    const val GEOGRAPHY = "Geography_Page"
    const val SECRET = "Secret_Page"
    const val ALGORITHM = "Algorithm"
    const val ADD_DEL_VOCAB = "AddDelVocab_Page"
    const val TESTING_VOCAB_NEW = "TestingVocab_Page_New"
    const val TESTING_VOCAB_REVISE = "TestingVocab_Page_Revise"
    const val TESTING_VOCAB_LEARNED = "TestingVocab_Page_Learned"
    const val TESTING_VOCAB_LEARNED_VIEW = "TestingVocab_Page_Learned_View"
}

@Composable
fun App(themeViewModel: ThemeViewModel = viewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sounds = remember { Sounds(context) }
    DisposableEffect(Unit) {
        onDispose { sounds.release() }
    }

    AppTheme(mode = themeViewModel.appThemeMode) {
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.HOME) {
                    Home_Page(navController)
                }

                composable(Routes.SETTINGS) {
                    Settings_Page(
                        navController,
                        currentMode = themeViewModel.appThemeMode,
                        onCycleTheme = themeViewModel::cycleThemeMode
                    )
                }

                composable(Routes.ENGLISH) {
                    English_Page(navController)
                }

                composable(Routes.GEOGRAPHY) {
                    Geography_Page(navController)
                }

                composable(Routes.SECRET) {
                    Secret_Page(
                        navController,
                        onPlayClick = { sounds.playClick() },
                        onOpenAlgorithm = { navController.navigate(Routes.ALGORITHM) }
                    )
                }

                composable(Routes.ALGORITHM) {
                    AlgorithmScreen(navController)
                }

                composable(Routes.ADD_DEL_VOCAB) {
                    AddDelVocab_Page(navController)
                }

                composable(Routes.TESTING_VOCAB_NEW) {
                    TestingVocab_Page(navController, testCategory = "NEW")
                }

                composable(Routes.TESTING_VOCAB_REVISE) {
                    TestingVocab_Page(navController, testCategory = "REVISE")
                }

                composable(Routes.TESTING_VOCAB_LEARNED) {
                    TestingVocab_Page(navController, testCategory = "LEARNED")
                }

                composable(Routes.TESTING_VOCAB_LEARNED_VIEW) {
                    TestingVocab_Page(navController, testCategory = "LEARNED_VIEW")
                }
            }
        }
    }
}

var m: Int = 0

/**
 * Minimized Home_Page.
 */
@Composable
fun Home_Page(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (m >= 5) {
                    navController.navigate(Routes.SECRET)
                } else {
                    m += 1
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            contentPadding = PaddingValues(0.dp)
        ) { Text(text = "Home", fontSize = 25.sp) }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate(Routes.SETTINGS) },
            modifier = Modifier.align(Alignment.End),
            contentPadding = PaddingValues(0.dp)
        ) { Text(text = "Settings", fontSize = 20.sp) }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), Arrangement.spacedBy(0.dp)) {
            Button(
                onClick = { navController.navigate(Routes.ENGLISH) },
                shape = RectangleShape,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
            ) { Text(text = "English", fontSize = 20.sp) }

            Button(
                onClick = { navController.navigate(Routes.GEOGRAPHY) },
                shape = RectangleShape,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
            ) { Text(text = "Geography", fontSize = 20.sp) }
        }
    }
}

/**
 * Converts Vocab list to JSON backup string.
 */
fun exportVocabToJson(vocabs: List<Vocab>): String {
    val jsonArray = JSONArray()
    for (vocab in vocabs) {
        val obj = JSONObject()
        obj.put("word", vocab.word)
        obj.put("translation", vocab.translation)
        obj.put("repetitions", vocab.repetitions)
        obj.put("intervalDays", vocab.intervalDays)
        obj.put("dueDay", vocab.dueDay)
        jsonArray.put(obj)
    }
    return jsonArray.toString(2)
}

/**
 * Parses JSON backup string back into Vocab list.
 */
fun parseVocabFromJson(jsonString: String): List<Vocab> {
    val list = mutableListOf<Vocab>()
    try {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val vocab = Vocab(
                word = obj.getString("word"),
                translation = obj.getString("translation"),
                repetitions = obj.optInt("repetitions", 0),
                intervalDays = obj.optInt("intervalDays", 0),
                dueDay = obj.optLong("dueDay", System.currentTimeMillis() / 86_400_000L)
            )
            list.add(vocab)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

/**
 * Settings Page with Theme toggle & Database Backup/Restore (Export & Import).
 */
@Composable
fun Settings_Page(
    navController: NavController,
    currentMode: AppThemeMode,
    onCycleTheme: () -> Unit,
    vocabViewModel: VocabViewModel = viewModel()
) {
    val context = LocalContext.current
    val allVocabs by vocabViewModel.allVocab.collectAsState()

    // System File Picker for Saving Backup JSON File
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                val jsonString = exportVocabToJson(allVocabs)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Exported ${allVocabs.size} words to backup!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // System File Picker for Opening/Restoring Backup JSON File
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader(Charsets.UTF_8).readText()
                } ?: ""

                val importedList = parseVocabFromJson(jsonString)
                if (importedList.isNotEmpty()) {
                    vocabViewModel.importVocabList(importedList) { count ->
                        Toast.makeText(context, "Imported $count words into database!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "No valid vocabulary data found in backup", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Import failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Settings:", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onCycleTheme, modifier = Modifier.fillMaxWidth()) {
            Text("Theme: ${currentMode.label}", fontSize = 16.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Database Backup & Restore Section
        Text(text = "Database Backup & Restore", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Export your vocabulary progress to a backup file before reinstalling or updating the app, and import it back afterwards to keep all your progress.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Export Backup Button
            Button(
                onClick = {
                    val defaultFileName = "vocab_backup_${System.currentTimeMillis() / 1000}.json"
                    exportLauncher.launch(defaultFileName)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Export Backup")
            }

            // Import Backup Button (Red Theme Error Color)
            Button(
                onClick = {
                    importLauncher.launch(arrayOf("application/json", "*/*"))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Import Backup")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.HOME, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }
}

/**
 * Add / Delete Vocabulary Subpage containing VocabManagerCard.
 */
@Composable
fun AddDelVocab_Page(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Vocabulary Manager", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Vocab add/delete card connected to Room SQLite
        VocabManagerCard()

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.ENGLISH, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }
}

/**
 * English Page with 4 Revision Categories and top Add/Delete button.
 */
@Composable
fun English_Page(navController: NavController, vocabViewModel: VocabViewModel = viewModel()) {
    val allVocabs by vocabViewModel.allVocab.collectAsState()
    val today = System.currentTimeMillis() / 86_400_000L

    // Filter SQLite vocabs into categories
    val newVocabs = remember(allVocabs) { allVocabs.filter { it.repetitions == 0 } }
    val reviseVocabs = remember(allVocabs, today) {
        allVocabs.filter { it.repetitions in 1..6 && it.dueDay <= today }
    }
    val learnedVocabs = remember(allVocabs) { allVocabs.filter { it.repetitions >= 7 } }
    val browseVocabs = remember(allVocabs) {
        if (learnedVocabs.isNotEmpty()) learnedVocabs else allVocabs.filter { it.repetitions > 0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Vocabulary Trainer", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Top Full-Width Button leading to Add/Delete Subpage
        Button(
            onClick = { navController.navigate(Routes.ADD_DEL_VOCAB) },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape
        ) {
            Text("Add / Delete Vocabulary", fontSize = 18.sp)
        }

        // Category 1: New Words (repetitions == 0)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "New Words", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${newVocabs.size} new words ready to learn", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_NEW) }) {
                    Text("Learn New Words")
                }
            }
        }

        // Category 2: Revise Words (repetitions in 1..6 & dueDay <= today)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Revise Words", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${reviseVocabs.size} words due today", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_REVISE) }) {
                    Text("Revise Due Words")
                }
            }
        }

        // Category 3: Practice Learned Words (repetitions >= 7 - modifies algorithm if mistake)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Practice Learned Words", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${learnedVocabs.size} words completed", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_LEARNED) }) {
                    Text("Practice Learned Words")
                }
            }
        }

        // Category 4: Browse Learned Words Viewer (repetitions >= 7 - no algorithm modification)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Browse Learned Words", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${browseVocabs.size} words available to review", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_LEARNED_VIEW) }) {
                    Text("Browse Learned Words")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.HOME, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }
}

/**
 * Flashcard Testing Page for Vocab ("TestingVocab_Page").
 * Shows word in a big card, "Show Translation" button reveals answer and replaces with "I knew" and "I didn't".
 * Automatically advances through all words dynamically until completion.
 */
@Composable
fun TestingVocab_Page(
    navController: NavController,
    testCategory: String, // "NEW", "REVISE", "LEARNED", or "LEARNED_VIEW"
    vocabViewModel: VocabViewModel = viewModel()
) {
    val allVocabs by vocabViewModel.allVocab.collectAsState()
    val today = System.currentTimeMillis() / 86_400_000L

    // Dynamically filter matching words for this category
    val currentQueue = remember(allVocabs, testCategory, today) {
        when (testCategory) {
            "NEW" -> allVocabs.filter { it.repetitions == 0 }
            "REVISE" -> allVocabs.filter { it.repetitions in 1..6 && it.dueDay <= today }
            "LEARNED" -> allVocabs.filter { it.repetitions >= 7 }
            "LEARNED_VIEW" -> {
                val learned = allVocabs.filter { it.repetitions >= 7 }
                if (learned.isNotEmpty()) learned else allVocabs.filter { it.repetitions > 0 }
            }
            else -> allVocabs
        }
    }

    var viewIndex by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Testing: $testCategory Words",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (currentQueue.isNotEmpty()) {
            val activeIndex = if (testCategory == "LEARNED_VIEW") {
                viewIndex.coerceAtMost(currentQueue.size - 1)
            } else {
                0 // In NEW, REVISE, and LEARNED, position 0 always points to the next active word!
            }

            if (activeIndex < currentQueue.size) {
                val currentVocab = currentQueue[activeIndex]

                // Big Flashcard Rectangle Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentVocab.word,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (showAnswer) {
                            Text(
                                text = currentVocab.translation,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "(Repetitions: ${currentVocab.repetitions})",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Buttons: "Show Translation" vs "I knew" / "I didn't"
                if (!showAnswer) {
                    Button(
                        onClick = { showAnswer = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Show Translation", fontSize = 18.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // "I knew" button (p = true)
                        Button(
                            onClick = {
                                if (testCategory != "LEARNED_VIEW") {
                                    val updated = calculateNextReview(currentVocab, knewIt = true)
                                    vocabViewModel.updateVocab(updated)
                                } else {
                                    viewIndex++
                                }
                                showAnswer = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Text("I knew", fontSize = 16.sp)
                        }

                        // "I didn't" button (p = false)
                        Button(
                            onClick = {
                                if (testCategory != "LEARNED_VIEW") {
                                    val updated = calculateNextReview(currentVocab, knewIt = false)
                                    vocabViewModel.updateVocab(updated)
                                } else {
                                    viewIndex++
                                }
                                showAnswer = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Text("I didn't", fontSize = 16.sp)
                        }
                    }
                }
            } else {
                // All words completed
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Your done with learing :)",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (testCategory == "LEARNED_VIEW") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewIndex = 0; showAnswer = false }) {
                                Text("Restart Browsing")
                            }
                        }
                    }
                }
            }
        } else {
            // Queue is empty
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your done with learing :)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.ENGLISH, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }
}

@Composable
fun Geography_Page(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(text = "World map knowledge quiz:", fontSize = 24.sp)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.HOME, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }
}

/**
 * Secret_Page containing extra play & debug buttons moved from Home,
 * plus a confirmation dialog for deleting all saved vocabulary words.
 */
@Composable
fun Secret_Page(
    navController: NavController,
    onPlayClick: () -> Unit,
    onOpenAlgorithm: () -> Unit,
    vocabViewModel: VocabViewModel = viewModel()
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteConfirmationInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Secret Page", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.eath_scroll),
                contentDescription = "earth_scroll_from_Mc",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onPlayClick() },
                filterQuality = FilterQuality.None
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPlayClick) {
            Text(text = "Play Sound")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AlgorithmButton(onOpenAlgorithm = onOpenAlgorithm)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { Toast.makeText(context, "m = $m", Toast.LENGTH_SHORT).show() }) {
            Text(text = "print m")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { m += 1 }) {
            Text(text = "output m", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Small Red Button: Delete All Saved Words
        Button(
            onClick = {
                deleteConfirmationInput = ""
                showDeleteDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Delete all saved words", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.HOME, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
    }

    // Confirmation AlertDialog requiring user to type 'delete'
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Database Wiping") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Type 'delete' below to confirm clearing all saved vocabulary words from SQLite database.",
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = deleteConfirmationInput,
                        onValueChange = { deleteConfirmationInput = it },
                        label = { Text("Type 'delete'") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteConfirmationInput.trim().equals("delete", ignoreCase = true)) {
                            vocabViewModel.deleteAllVocabs {
                                Toast.makeText(context, "All saved vocabulary words deleted", Toast.LENGTH_LONG).show()
                            }
                            showDeleteDialog = false
                        }
                    },
                    enabled = deleteConfirmationInput.trim().equals("delete", ignoreCase = true),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppTheme(mode = AppThemeMode.DEFAULT) {
        val navController = rememberNavController()
        Home_Page(navController = navController)
    }
}
