package com.example.kidsmathcoach.models

import com.example.kidsmathcoach.enums.Operation

data class Settings(
    var username: String,
    var difficultyLevel: Int,
    var lastOperation: Operation,
    var correctAnswers: Int,
    var incorrectAnswers: Int
)