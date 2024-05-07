package com.example.kidsmathcoach

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
//import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidsmathcoach.ui.theme.KidsMathCoachTheme
//import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
//import androidx.navigation.NavHost


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KidsMathCoachTheme {
                //MainScreen()
                // В вашем фрагменте или активити, где происходит навигация между экранами:
                val navController = rememberNavController()

                NavHost(navController, startDestination = "MainScreen") {
                    composable("MainScreen") {
                        MainScreen(navController)
                    }
                    composable("SettingsScreen") {
                        SettingsScreen(navController)
                    }
                }
            }
        }
    }
}



@Composable
fun MainScreen(navController: NavController) {
    var operation by remember { mutableStateOf(Operation.ADD) }
    var num1 by remember { mutableIntStateOf((0..10).random()) }
    var num2 by remember { mutableIntStateOf((0..10).random()) }
    var answer by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var isCorrect by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    Column( //Кнопка настроек
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End
    ) {
        Button(
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
            "Тренажер\n по математике\nдля Дюшки!!!\n",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1.copy(fontSize = 28.sp)
        )

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
                Text("Дюшка молодец!",
                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp))
            } else {
                Text(
                    buildAnnotatedString {
                        append("Дюшка подумай!\n Правильный ответ: ")
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




@Composable
fun SettingsScreen(navController: NavController) {
    // Ваш код для создания экрана настроек
    Button(onClick = { /*onSaveSettings()*/ }) {
        Text(text = "Сохранить настройки")
    }
}




//fun saveSettingsToStorage(settings: YourSettingsModel) {
//    // Ваш код для сохранения настроек в файл
//}

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
