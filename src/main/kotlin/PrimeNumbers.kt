package org.example

fun isPrime(number: Int): Boolean {
    if (number < 2) {
        return false
    }

    var divisor = 2
    while (divisor * divisor <= number) {
        if (number % divisor == 0) {
            return false
        }
        divisor++
    }

    return true
}

fun runPrimeNumbers() {
    println("Введите количество простых чисел:")
    val requiredCount = readln().toInt()

    var foundCount = 0
    var candidate = 2

    while (foundCount < requiredCount) {
        if (isPrime(candidate)) {
            foundCount++
            val suffix = if (foundCount == 1 || foundCount == 2) "-ое" else "-е"
            println("$foundCount$suffix число: $candidate")
        }
        candidate++
    }
}
