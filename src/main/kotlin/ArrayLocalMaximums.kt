package org.example

fun runArrayLocalMaximums() {
    val numbers = intArrayOf(1, 5, 2, 8, 3, 7, 4)

    print("Результат for: ")
    for (index in 1 until numbers.lastIndex) {
        if (numbers[index] > numbers[index - 1] && numbers[index] > numbers[index + 1]) {
            print("${numbers[index]} ")
        }
    }
    println()

    print("Результат while: ")
    var index = 1
    while (index < numbers.lastIndex) {
        if (numbers[index] > numbers[index - 1] && numbers[index] > numbers[index + 1]) {
            print("${numbers[index]} ")
        }
        index++
    }
    println()

    print("Результат forEach: ")
    (1 until numbers.lastIndex).forEach { currentIndex ->
        if (numbers[currentIndex] > numbers[currentIndex - 1] &&
            numbers[currentIndex] > numbers[currentIndex + 1]
        ) {
            print("${numbers[currentIndex]} ")
        }
    }
    println()
}
