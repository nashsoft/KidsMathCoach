package com.example.kidsmathcoach

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.example.kidsmathcoach.ui.theme.Green
import com.example.kidsmathcoach.ui.theme.Red
//import androidx.navigation.NavHost
import com.google.gson.Gson
import java.io.File
import kotlin.math.pow


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Сохранение настроек по-умолчанию (при первом запуске приложения после установки)
        val initSettingsFileName = File(filesDir, "settings.json")
        val initSettingsFilePath = File(filesDir, "settings.json").absolutePath
        if (!initSettingsFileName.exists()) {
            val initSettings = Settings("Username", 1, Operation.ADD, 0, 0)
            saveSettingsToFile(this, initSettings, initSettingsFilePath)
        }

        setContent {
            KidsMathCoachTheme {
                val settingsFilePath = File(filesDir, "settings.json").absolutePath
                // Загрузка первоначальных настроек
                //var loadedSettings = loadSettingsFromFile(this, settingsFilePath)

                val navController = rememberNavController()

                NavHost(navController, startDestination = "MainScreen") {
                    composable("MainScreen") {
                        //Повторная загрузка настроек
                        val loadedSettings = loadSettingsFromFile(this@MainActivity, settingsFilePath)
                        MainScreen(navController, loadedSettings)
                    }
                    composable("SettingsScreen") {
                        //Повторная загрузка настроек
                        val loadedSettings = loadSettingsFromFile(this@MainActivity, settingsFilePath)
                        SettingsScreen(navController, this@MainActivity, loadedSettings, settingsFilePath)
                    }
                }
            }
        }
    }
}

fun navigateBackToMainScreen(navController: NavController) {
    navController.popBackStack()
}

data class Settings(
    var username: String,
    var difficultyLevel: Int,
    var lastOperation: Operation,
    var correctAnswers: Int,
    var incorrectAnswers: Int
)

fun saveSettingsToFile(context: Context, settings: Settings, filePath: String) { //Сохранение настроек в файл
    val json = Gson().toJson(settings)
    File(filePath).writeText(json)
}

fun loadSettingsFromFile(context: Context, filePath: String): Settings { //Загрузка настроек из файла
    val json = File(filePath).readText()
    return Gson().fromJson(json, Settings::class.java)
}

enum class DifficultyLevel(val level: Int) {
    X1(1) {
//        override fun calculate(num1: Int, num2: Int) = num1 + num2
    },
    X2(2) {
//        override fun calculate(num1: Int, num2: Int) = num1 - num2
    },
    X3(3) {
//        override fun calculate(num1: Int, num2: Int) = num1 * num2
    };

//    abstract fun calculate(num1: Int, num2: Int): Int
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
    };

    abstract fun calculate(num1: Int, num2: Int): Int
}


@Composable
fun MainScreen(navController: NavController, settings: Settings) {
    var operation by remember { mutableStateOf(settings.lastOperation) }
    var num by remember { mutableIntStateOf(10.0.pow(settings.difficultyLevel.toDouble()).toInt()) }
    var num1 by remember { mutableIntStateOf((0..num).random()) }
    var num2 by remember { mutableIntStateOf((0..num1).random()) }
    var answer by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var isCorrect by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var previousAnswer by remember { mutableStateOf("") }//Отслеживаем, чтобы не было накрутки результатов

    val context = LocalContext.current

    var correctAnswers by remember { mutableStateOf(settings.correctAnswers) }
    var incorrectAnswers by remember { mutableStateOf(settings.incorrectAnswers) }


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
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
         Text(//Имя пользователя
         "${settings.username}\n",
         modifier = Modifier.fillMaxWidth(),
         textAlign = TextAlign.Center,
         style = MaterialTheme.typography.body1.copy(fontSize = 30.sp)
         )

        // Расчет и отображение пропорции правильных и неправильных ответов
        var totalAnswers = correctAnswers + incorrectAnswers
        var correctFraction = if (totalAnswers > 0) correctAnswers.toFloat() / totalAnswers else 0f
        var incorrectFraction = if (totalAnswers > 0) incorrectAnswers.toFloat() / totalAnswers else 0f

        // Отображение количества правильных и неправильных ответов
        if (totalAnswers>0) {Text("Прогресс", style = TextStyle(fontSize = 18.sp))}
        //Text("Правильно: $correctAnswers      Неправильно: $incorrectAnswers", style = TextStyle(fontSize = 20.sp))

        ProportionBar(correctFraction, incorrectFraction, correctAnswers, incorrectAnswers)


        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Текст примера для решения
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
            Spacer(modifier = Modifier.width(10.dp))

            // Отображение результата проверки или знак вопроса
            if (answer.isNotEmpty() && isChecked) {
                if (isCorrect) {
//                Text("А ${settings.username} то молодец!",
//                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_yes),
                        contentDescription = "Правильный ответ",
                        modifier = Modifier.size(64.dp)
                    )
                } else {
//                Text(
//                    buildAnnotatedString {
//                        append("${settings.username}, подумай!\n Правильный ответ: ")
//                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                            append("$correctAnswer")
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
//                )
                    Image(
                        painter = painterResource(id = R.drawable.ic_no),
                        contentDescription = "Неправильный ответ",
                        modifier = Modifier.size(64.dp)
                    )
                }
            } else {
                Image(
                painter = painterResource(id = R.drawable.ic_question),
                contentDescription = "Вопрос",
                modifier = Modifier.size(64.dp)
            )}
        }


        // Кнопка для проверки ответа
        Button(
            onClick = {
                if (!isChecked || answer != previousAnswer) { //Избегаем накруток (повторных ответов)
                    correctAnswer = operation.calculate(num1, num2)
                    isCorrect = answer.toIntOrNull() == correctAnswer
                    isChecked = true
                    previousAnswer = answer

                    if (isCorrect) {
                        correctAnswers += 1
                        settings.correctAnswers = correctAnswers
                    } else {
                        incorrectAnswers += 1
                        settings.incorrectAnswers = incorrectAnswers
                    }
                    // Сохранение обновленных настроек
                    val settingsFilePath = File(context.filesDir, "settings.json").absolutePath
                    saveSettingsToFile(context, settings, settingsFilePath)
                }

            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Проверить")
        }


        // Кнопка для генерации нового примера
        Button(
            onClick = {
                num1 = (0..num).random()
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
    var difficultyLevel by remember { mutableIntStateOf(settings.difficultyLevel) }
    var correctAnswers by remember { mutableStateOf(settings.correctAnswers) }
    var incorrectAnswers by remember { mutableStateOf(settings.incorrectAnswers) }

    var isOperationMenuExpanded by remember { mutableStateOf(false) }
    var isDifficultyMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Text("Настройки:",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1.copy(fontSize = 28.sp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Отображение количества правильных и неправильных ответов
        //Text("Правильных ответов: $correctAnswers", style = TextStyle(fontSize = 20.sp))
        //Text("Неправильных ответов: $incorrectAnswers", style = TextStyle(fontSize = 20.sp))

        // Расчет и отображение пропорции правильных и неправильных ответов
        var totalAnswers = correctAnswers + incorrectAnswers
        var correctFraction = if (totalAnswers > 0) correctAnswers.toFloat() / totalAnswers else 0f
        var incorrectFraction = if (totalAnswers > 0) incorrectAnswers.toFloat() / totalAnswers else 0f

        if (totalAnswers>0) {Text("Прогресс пользователя:", style = TextStyle(fontSize = 18.sp))}
        ProportionBar(correctFraction, incorrectFraction, correctAnswers, incorrectAnswers)

        Button(//Кнопка сброса статистики
            onClick = {
                // Сбросить количество правильных и неправильных ответов
                val updatedSettings = settings.copy(correctAnswers = 0, incorrectAnswers = 0)
                // Сохранить обновленные настройки
                saveSettingsToFile(context, updatedSettings, settingsFilePath)
                // Обновить значения переменных в состоянии
                correctAnswers = 0
                incorrectAnswers = 0
            }
        ) {
            Text(text = "Сбросить статистику")
        }
        Spacer(modifier = Modifier.height(20.dp))

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
                expanded = isOperationMenuExpanded,
                onDismissRequest = { isOperationMenuExpanded = false },
                modifier = Modifier.padding(8.dp),
            ) {
                Operation.entries.forEach { operationType ->
                    DropdownMenuItem(
                        onClick = {
                            operation = operationType
                            isOperationMenuExpanded = false
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
                modifier = Modifier.clickable { isOperationMenuExpanded = true },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            )
        }

        //Выбор сложности
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Сложность: ",
                modifier = Modifier,
                style = MaterialTheme.typography.body1.copy(fontSize = 26.sp))
            DropdownMenu(
                expanded = isDifficultyMenuExpanded,
                onDismissRequest = { isDifficultyMenuExpanded = false },
                modifier = Modifier.padding(8.dp),
            ) {
                DifficultyLevel.entries.forEach { difficultyLevelType ->
                    DropdownMenuItem(
                        onClick = {
                            difficultyLevel = difficultyLevelType.level
                            isDifficultyMenuExpanded = false
                        }
                    ) {
                        Text("  ${difficultyLevelType.level}  ",
                            modifier = Modifier,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            ))
                    }
                }
            }
            Text(
                difficultyLevel.toString(),
                modifier = Modifier.clickable { isDifficultyMenuExpanded = true },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            )
            Text(" разрядная",
                modifier = Modifier,
                style = MaterialTheme.typography.body1.copy(fontSize = 26.sp))
        }

        //Кнопки отмены и сохранения
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Button(onClick = { //Отмена сохранения настроек
                //Возвращаемся на главный экран
                navigateBackToMainScreen(navController)
            }) {
                Text(text = "Отмена")
            }
            Spacer(modifier = Modifier.width(50.dp))
            Button(onClick = { // Сохранение настроек
                var updatedSettings = settings.copy(username = username, lastOperation = operation, difficultyLevel = difficultyLevel, correctAnswers = correctAnswers, incorrectAnswers = incorrectAnswers)
                saveSettingsToFile(context, updatedSettings, settingsFilePath)
                //Возвращаемся на главный экран
                navigateBackToMainScreen(navController)
            }) {
                Text(text = "Сохранить")
            }
        }
    }
}


//Отображение пропорций ответов без текста
@Composable
fun ProportionBar(correctFraction: Float, incorrectFraction: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        val width = size.width
        val height = size.height
        val totalWidth = width * (correctFraction + incorrectFraction)

        var currentX = 0f

        drawRect(
            color = Green,
            topLeft = Offset(currentX, 0f),
            size = Size(width * correctFraction, height)
        )

        currentX += width * correctFraction

        drawRect(
            color = Red,
            topLeft = Offset(currentX, 0f),
            size = Size(width * incorrectFraction, height)
        )
    }
}


//Отображение пропорций ответов с текстом
@Composable
fun ProportionBar(correctFraction: Float, incorrectFraction: Float, correctAnswers: Int, incorrectAnswers: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        val width = size.width
        val height = size.height
        val totalWidth = width * (correctFraction + incorrectFraction)

        var currentX = 0f

        // Рисуем зеленую область
        drawRect(
            color = Green,
            topLeft = Offset(currentX, 0f),
            size = Size(width * correctFraction, height)
        )

        // Рисуем надпись в зеленой области
        if (correctAnswers>0) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "$correctAnswers",
                    width * correctFraction / 2, // Позиция по оси X (по центру ширины закрашенной области)
                    (height / 2) + 20, // Позиция по оси Y (по центру высоты области)
                    Paint().apply {
                        color = Color.White.toArgb() // Цвет текста
                        textSize = 60f // Размер текста
                        textAlign = Paint.Align.CENTER // Выравнивание текста по центру
                    }
                )
            }
        }

        currentX += width * correctFraction

        // Рисуем красную область
        drawRect(
            color = Red,
            topLeft = Offset(currentX, 0f),
            size = Size(width * incorrectFraction, height)
        )

        // Рисуем надпись в красной области
        if (incorrectAnswers>0) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "$incorrectAnswers",
                    currentX + width * incorrectFraction / 2, // Позиция по оси X (по центру ширины закрашенной области)
                    (height / 2) + 20, // Позиция по оси Y (по центру высоты области)
                    Paint().apply {
                        color = Color.White.toArgb() // Цвет текста
                        textSize = 60f // Размер текста
                        textAlign = Paint.Align.CENTER // Выравнивание текста по центру
                    }
                )
            }
        }
    }
}
