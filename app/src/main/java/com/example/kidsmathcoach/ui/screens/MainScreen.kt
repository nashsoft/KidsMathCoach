package com.example.kidsmathcoach.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidsmathcoach.ui.components.ProportionBar
import com.example.kidsmathcoach.models.Settings
import com.example.kidsmathcoach.repository.SettingsRepository
import kotlin.math.pow
import com.example.kidsmathcoach.R


@Composable
fun MainScreen(navController: NavController, settings: Settings, repository: SettingsRepository) {
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
        horizontalAlignment = Alignment.CenterHorizontally
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

        // Отображение количества правильных и неправильных ответов только когда есть ответы
        if (totalAnswers>0) {
            Text("Правильно                |            Неправильно", style = TextStyle(fontSize = 18.sp))
            ProportionBar(correctFraction, incorrectFraction, correctAnswers, incorrectAnswers)
        }

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

            // Отображение результата проверки ответа или просто знак вопроса
            if (answer.isNotEmpty() && isChecked) {
                if (isCorrect) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_yes),
                        contentDescription = "Правильный ответ",
                        modifier = Modifier.size(64.dp)
                    )
                } else {
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
                    repository.saveSettings(settings)
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
            Text("Новый пример")
        }
    }
}