package org.example

import kotlin.math.sqrt

fun sqr(number: Double): Double = number * number

fun discriminant(a: Double, b: Double, c: Double): Double = sqr(b) - 4 * a * c

fun rootsNumber(a: Double, b: Double, c: Double): Int {
    val value = discriminant(a, b, c)

    return when {
        value > 0 -> 2
        value == 0.0 -> 1
        else -> 0
    }
}

fun quadraticRoot(a: Double, b: Double, c: Double) {
    val value = discriminant(a, b, c)

    when (rootsNumber(a, b, c)) {
        2 -> {
            val firstRoot = (-b + sqrt(value)) / (2 * a)
            val secondRoot = (-b - sqrt(value)) / (2 * a)
            println("Корни уравнения: x1 = $firstRoot, x2 = $secondRoot")
        }

        1 -> {
            val root = -b / (2 * a)
            println("Корень уравнения: x = $root")
        }

        else -> println("Действительных корней нет")
    }
}

fun runQuadraticEquation() {
    val a = 1.0
    val b = -5.0
    val c = 6.0

    println("Квадрат числа b: ${sqr(b)}")
    println("Дискриминант: ${discriminant(a, b, c)}")
    println("Количество действительных корней: ${rootsNumber(a, b, c)}")
    quadraticRoot(a, b, c)
}
