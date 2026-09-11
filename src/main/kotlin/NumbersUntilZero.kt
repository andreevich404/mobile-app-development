package org.example

fun runNumbersUntilZero() {
    var count = 0
    var sum = 0.0

    println("Вводите числа по одному. Для завершения введите 0:")

    while (true) {
        val number = readln().toDouble()

        if (number == 0.0) {
            break
        }

        count++
        sum += number
    }

    println("Количество введённых чисел: $count")
    println("Сумма введённых чисел: $sum")

    if (count == 0) {
        println("Среднее арифметическое: нет введённых чисел")
    } else {
        println("Среднее арифметическое: ${sum / count}")
    }
}
