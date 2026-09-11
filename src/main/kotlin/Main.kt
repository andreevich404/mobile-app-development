package org.example

fun main() {
    println("Выберите задание лабораторной работы №0 (1–10):")

    when (readln().toInt()) {
        1 -> runFirstAndLastDigit()
        2 -> runNumbersUntilZero()
        3 -> runGuessTheNumber()
        4 -> runPrimeNumbers()
        5 -> runArrayLocalMaximums()
        6 -> runArrayStatistics()
        7 -> runQuadraticEquation()
        8 -> runArrayProcessor()
        9 -> runVector()
        10 -> runVehicle()
        else -> println("Задания с таким номером нет")
    }
}
