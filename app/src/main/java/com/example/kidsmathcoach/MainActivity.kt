package com.example.kidsmathcoach

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidsmathcoach.ui.theme.KidsMathCoachTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
//import androidx.navigation.NavHost
import com.google.gson.Gson
import java.io.File
import kotlin.math.pow


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Сохранение настроек по-умолчанию
        val initSettingsFileName = File(filesDir, "settings.json")
        val initSettingsFilePath = File(filesDir, "settings.json").absolutePath
        if (!initSettingsFileName.exists()) {
            var initSettings = Settings("Username", 2, Operation.ADD, 0, 0)
            saveSettingsToFile(this, initSettings, initSettingsFilePath)
        }

        setContent {
            KidsMathCoachTheme {
                //val settingsFileName = "settings.json"
                val settingsFilePath = File(filesDir, "settings.json").absolutePath
                // Загрузка первоначальных настроек
                var loadedSettings = loadSettingsFromFile(this, settingsFilePath)

                val navController = rememberNavController()

                NavHost(navController, startDestination = "MainScreen") {
                    composable("MainScreen") {
                        //Повторная загрузка настроек
                        var loadedSettings = loadSettingsFromFile(this@MainActivity, settingsFilePath)
                        MainScreen(navController, loadedSettings)
                    }
                    composable("SettingsScreen") {
                        //Повторная загрузка настроек
                        var loadedSettings = loadSettingsFromFile(this@MainActivity, settingsFilePath)
                        SettingsScreen(navController, this@MainActivity, loadedSettings, settingsFilePath)
                    }
                }
            }
        }
    }
}

fun saveSettingsToFile(context: Context, settings: Settings, filePath: String) { //Сохранение настроек в файл
    val json = Gson().toJson(settings)
    File(filePath).writeText(json)
}

fun loadSettingsFromFile(context: Context, filePath: String): Settings { //Считывание настроек из файла
    val json = File(filePath).readText()
    return Gson().fromJson(json, Settings::class.java)
}

@Composable
fun MainScreen(navController: NavController, settings: Settings) {
    var operation by remember { mutableStateOf(settings.lastOperation) }
    var num by remember { mutableIntStateOf(10.0.pow(settings.difficultyLevel.toDouble()).toInt()) }
    var num1 by remember { mutableIntStateOf((0..num).random()) }
    var num2 by remember { mutableIntStateOf((0..num).random()) }
    var answer by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var isCorrect by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End
    ) {
        Button( //Кнопка отображения настроек
            onClick = {
                navController.navigate("SettingsScreen")
            },
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            contentPadding = PaddingValues(5.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings",
                modifier = Modifier.size(30.dp)
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            "Тренажер\n по математике\nдля ${settings.username}!!!\n",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1.copy(fontSize = 28.sp)
        )
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("$num1  ${operation.symbol}  $num2  =  ",
                style = MaterialTheme.typography.body1.copy(fontSize = 26.sp))


        // Поле для ввода ответа
        OutlinedTextField(
            value = answer,
            onValueChange = {
                // Проверяем, что новое значение состоит только из цифр
                if (it.all { char -> char.isDigit() }) {
                    answer = it
                }
                isChecked = false
                            },
            label = { Text("Ваш ответ") },
            modifier = Modifier.width(115.dp),
            textStyle = TextStyle(fontSize = 24.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        }
        // Кнопка для проверки ответа
        Button(
            onClick = {
                correctAnswer = operation.calculate(num1, num2)
                isCorrect = answer.toIntOrNull() == correctAnswer
                isChecked = true
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Проверить")
        }

        // Отображение результата проверки
        if (answer.isNotEmpty() && isChecked) {
            if (isCorrect) {
                Text("А ${settings.username} то молодец!",
                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp))
            } else {
                Text(
                    buildAnnotatedString {
                        append("${settings.username}, подумай!\n Правильный ответ: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$correctAnswer")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
                )

            }
        }

        // Кнопка для генерации нового примера
        Button(
            onClick = {
                num1 = (0..10).random()
                num2 = (0..num1).random()
                answer = ""
                isCorrect = false
                isChecked = false
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Другой пример")
        }
    }
}





//Экран настроек
@Composable
fun SettingsScreen(navController: NavController,
                   context: Context,
                   settings: Settings,
                   settingsFilePath: String) {

    var username by remember { mutableStateOf(settings.username) }
    var operation by remember { mutableStateOf(settings.lastOperation) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        // Поле для ввода имени пользователя
        OutlinedTextField(
            value = username,
            onValueChange = { newValue ->
                username = newValue
            },
            label = { Text("Ваше имя:") },
            modifier = Modifier.width(250.dp),
            textStyle = TextStyle(fontSize = 24.sp),
        )
        //Выбор математической операции
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Операция: ",
                modifier = Modifier,
                style = MaterialTheme.typography.body1.copy(fontSize = 26.sp))
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.padding(8.dp),
            ) {
                Operation.entries.forEach { operationType ->
                    DropdownMenuItem(
                        onClick = {
                            operation = operationType
                            isMenuExpanded = false
                        }
                    ) {
                        Text("  ${operationType.symbol}  ",
                            modifier = Modifier,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            ))
                    }
                }
            }
            Text(
                operation.symbol,
                modifier = Modifier.clickable { isMenuExpanded = true },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            )
        }
        Button(onClick = { // Сохранение настроек
            var updatedSettings = settings.copy(username = username, lastOperation = operation)
            saveSettingsToFile(context, updatedSettings, settingsFilePath)
            //Возвращаемся на главный экран
            navigateBackToMainScreen(navController)
        }) {
            Text(text = "Сохранить настройки")
        }
    }
}





fun navigateBackToMainScreen(navController: NavController) {
    navController.popBackStack()
}


enum class Operation(val symbol: String) {
    ADD("+") {
        override fun calculate(num1: Int, num2: Int) = num1 + num2
    },
    SUB("-") {
        override fun calculate(num1: Int, num2: Int) = num1 - num2
    },
    MUL("*") {
        override fun calculate(num1: Int, num2: Int) = num1 * num2
    }/*,
    DIV("/") {
        override fun calculate(num1: Int, num2: Int) = num1 / num2
    }*/;

    abstract fun calculate(num1: Int, num2: Int): Int
}

data class Settings(
    var username: String,
    var difficultyLevel: Int,
    var lastOperation: Operation,
    var correctAnswers: Int,
    var incorrectAnswers: Int
)