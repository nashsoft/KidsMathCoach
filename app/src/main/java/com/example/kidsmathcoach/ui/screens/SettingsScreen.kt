package com.example.kidsmathcoach.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidsmathcoach.enums.DifficultyLevel
import com.example.kidsmathcoach.enums.Operation
import com.example.kidsmathcoach.ui.components.ProportionBar
import com.example.kidsmathcoach.models.Settings
import com.example.kidsmathcoach.repository.SettingsRepository


//Экран настроек
@Composable
fun SettingsScreen(
    navController: NavController,
    context: Context,
    settings: Settings,
    repository: SettingsRepository
) {
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Настройки:",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1.copy(fontSize = 28.sp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Расчет и отображение пропорции правильных и неправильных ответов
        var totalAnswers = correctAnswers + incorrectAnswers
        var correctFraction = if (totalAnswers > 0) correctAnswers.toFloat() / totalAnswers else 0f
        var incorrectFraction = if (totalAnswers > 0) incorrectAnswers.toFloat() / totalAnswers else 0f

        if (totalAnswers>0) {
            Text("Прогресс пользователя:", style = TextStyle(fontSize = 18.sp))
        }
        ProportionBar(correctFraction, incorrectFraction, correctAnswers, incorrectAnswers)

        Button(//Кнопка сброса статистики
            onClick = {
                // Сбросить количество правильных и неправильных ответов
                val updatedSettings = settings.copy(correctAnswers = 0, incorrectAnswers = 0)
                // Сохранить обновленные настройки
                repository.saveSettings(updatedSettings)
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
                            )
                        )
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
                            )
                        )
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
                //Просто возвращаемся на главный экран без сохранения настроек
                navController.popBackStack()
            }) {
                Text(text = "Отмена")
            }
            Spacer(modifier = Modifier.width(50.dp))
            Button(
                onClick = { //Сохранение настроек и возращение на основной экран
                    settings.username = username
                    settings.lastOperation = operation
                    settings.difficultyLevel = difficultyLevel
                    settings.correctAnswers = correctAnswers
                    settings.incorrectAnswers = incorrectAnswers

                    repository.saveSettings(settings)

                    navController.navigate("MainScreen")
                }
            ) {
                Text("Сохранить")
            }
        }
    }
}