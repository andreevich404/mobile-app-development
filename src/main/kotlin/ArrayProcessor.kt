package org.example

class ArrayProcessor(private val numbers: IntArray) {
    fun positiveSum(): Int {
        var sum = 0
        numbers.forEach { number ->
            if (number > 0) {
                sum += number
            }
        }
        return sum
    }

    fun product(): Int {
        var result = 1
        numbers.forEach { number -> result *= number }
        return result
    }

    fun average(): Double {
        if (numbers.isEmpty()) {
            return 0.0
        }

        var sum = 0
        numbers.forEach { number -> sum += number }
        return sum.toDouble() / numbers.size
    }
}

fun runArrayProcessor() {
    val numbers = intArrayOf(2, -3, 4, 5)
    val processor = ArrayProcessor(numbers)

    println("Сумма положительных элементов: ${processor.positiveSum()}")
    println("Произведение элементов: ${processor.product()}")
    println("Среднее арифметическое: ${processor.average()}")
}
