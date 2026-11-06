package com.example.learnigapp

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalInspectionMode
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
                    Secret_Page(navController,
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
            }
        }
    }
}

var m: Int = 0

@Composable
fun Home_Page(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .padding(bottom = 0.dp),
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

@Composable
fun Settings_Page(
    navController: NavController,
    currentMode: AppThemeMode,
    onCycleTheme: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Settings:", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onCycleTheme) {
            Text("Theme: ${currentMode.label}")
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
        ) { Text("Back to English") }
    }
}

/**
 * English Page with 3 Revision Categories and top Add/Delete button.
 */
@Composable
fun English_Page(navController: NavController, vocabViewModel: VocabViewModel = viewModel()) {
    val allVocabs by vocabViewModel.allVocab.collectAsState()
    val today = System.currentTimeMillis() / 86_400_000L

    // Filter SQLite vocabs into 3 categories
    val newVocabs = remember(allVocabs) { allVocabs.filter { it.repetitions == 0 } }
    val reviseVocabs = remember(allVocabs, today) {
        allVocabs.filter { it.repetitions in 1..6 && it.dueDay <= today }
    }
    val learnedVocabs = remember(allVocabs) { allVocabs.filter { it.repetitions >= 7 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.TESTING_VOCAB_NEW) },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "New Words (repetitions = 0)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${newVocabs.size} new words ready to learn", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_NEW) }) {
                    Text("Learn New Words")
                }
            }
        }

        // Category 2: Revise Words (repetitions in 1..6 & dueDay <= today)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.TESTING_VOCAB_REVISE) },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Revise Words (due for review)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${reviseVocabs.size} words due today", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_REVISE) }) {
                    Text("Revise Due Words")
                }
            }
        }

        // Category 3: Learned Words (repetitions >= 7)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Routes.TESTING_VOCAB_LEARNED) },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Learned Words (repetitions = 7+)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${learnedVocabs.size} words completed", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(Routes.TESTING_VOCAB_LEARNED) }) {
                    Text("Practice Learned Words")
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




/* * Flashcard Testing Page for Vocab ("TestingVocab_Page").
 * Shows word in a big card, "Show Translation" button reveals answer and replaces with "I knew" and "I didn't".
 */
@Composable
fun TestingVocab_Page(
    navController: NavController,
    testCategory: String, // "NEW", "REVISE", or "LEARNED"
    vocabViewModel: VocabViewModel = viewModel()
) {
    val allVocabs by vocabViewModel.allVocab.collectAsState()
    val today = System.currentTimeMillis() / 86_400_000L

    // Filter test list based on category
    val testList = remember(allVocabs, testCategory, today) {
        when (testCategory) {
            "NEW" -> allVocabs.filter { it.repetitions == 0 }
            "REVISE" -> allVocabs.filter { it.repetitions in 1..6 && it.dueDay <= today }
            "LEARNED" -> allVocabs.filter { it.repetitions >= 7 }
            else -> allVocabs
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
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

        if (testList.isNotEmpty() && currentIndex < testList.size) {
            val currentVocab = testList[currentIndex]

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
                            val updated = calculateNextReview(currentVocab, knewIt = true)
                            vocabViewModel.updateVocab(updated)
                            showAnswer = false
                            if (currentIndex + 1 < testList.size) {
                                currentIndex++
                            } else {
                                currentIndex = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("I knew", fontSize = 16.sp)
                    }

                    // "I didn't know" button (p = false)
                    Button(
                        onClick = {
                            val updated = calculateNextReview(currentVocab, knewIt = false)
                            vocabViewModel.updateVocab(updated)
                            showAnswer = false
                            if (currentIndex + 1 < testList.size) {
                                currentIndex++
                            } else {
                                currentIndex = 0
                            }
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
            // No words in this list
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
                    Text(text = "No words in '$testCategory' category right now!", fontSize = 18.sp)
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

@Composable
fun Secret_Page(navController: NavController, onPlayClick: () -> Unit, onOpenAlgorithm: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("You`re not supposed to see this")

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { Toast.makeText(context, "m = $m", Toast.LENGTH_SHORT).show() }) {
            Text(text = "print m")
        }

        Spacer(modifier = Modifier.weight(1f))

        AlgorithmButton(onOpenAlgorithm)

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onPlayClick) {
            Text(text = "Play")
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.eath_scroll),
            contentDescription = "earth_scroll_from_Mc",
            modifier = Modifier
                .size(64.dp)
                .clickable { onPlayClick() },
            filterQuality = FilterQuality.None
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack(Routes.HOME, false) },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Back") }
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