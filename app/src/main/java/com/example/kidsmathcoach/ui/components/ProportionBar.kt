package com.example.kidsmathcoach.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import com.example.kidsmathcoach.ui.theme.Green
import com.example.kidsmathcoach.ui.theme.Red


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

        // Рисуем зеленую область (пропорция правильных ответов)
        drawRect(
            color = Green,
            topLeft = Offset(currentX, 0f),
            size = Size(width * correctFraction, height)
        )

        // Рисуем надпись в зеленой области (кол-во ответов)
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

        // Рисуем красную область (пропорция неправильных ответов)
        drawRect(
            color = Red,
            topLeft = Offset(currentX, 0f),
            size = Size(width * incorrectFraction, height)
        )

        // Рисуем надпись в красной области (кол-во ответов)
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
