package org.example

fun runArrayStatistics() {
    val numbers = intArrayOf(3, -2, 4, 1)

    var productFor = 1
    var minFor = numbers[0]
    var maxFor = numbers[0]
    for (number in numbers) {
        productFor *= number
        if (number < minFor) minFor = number
        if (number > maxFor) maxFor = number
    }
    println("for: произведение = $productFor, min = $minFor, max = $maxFor")

    var productWhile = 1
    var minWhile = numbers[0]
    var maxWhile = numbers[0]
    var index = 0
    while (index < numbers.size) {
        val number = numbers[index]
        productWhile *= number
        if (number < minWhile) minWhile = number
        if (number > maxWhile) maxWhile = number
        index++
    }
    println("while: произведение = $productWhile, min = $minWhile, max = $maxWhile")

    var productForEach = 1
    var minForEach = numbers[0]
    var maxForEach = numbers[0]
    numbers.forEach { number ->
        productForEach *= number
        if (number < minForEach) minForEach = number
        if (number > maxForEach) maxForEach = number
    }
    println("forEach: произведение = $productForEach, min = $minForEach, max = $maxForEach")

    val productReduce = numbers.reduce { product, number -> product * number }
    val minimum = numbers.min()
    val maximum = numbers.max()
    println("reduce(), min(), max(): произведение = $productReduce, min = $minimum, max = $maximum")
}
