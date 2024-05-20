package com.example.kidsmathcoach.enums

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