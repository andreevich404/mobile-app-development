package org.example

import kotlin.math.sqrt

class Vector(val x: Double, val y: Double, val z: Double) {
    fun length(): Double = sqrt(x * x + y * y + z * z)

    fun dotProduct(other: Vector): Double = x * other.x + y * other.y + z * other.z

    infix fun dot(other: Vector): Double = dotProduct(other)

    operator fun times(other: Vector): Double = dotProduct(other)
}

fun scalarProduct(first: Vector, second: Vector): Double = first.dotProduct(second)

fun runVector() {
    val firstVector = Vector(1.0, 2.0, 3.0)
    val secondVector = Vector(3.0, 2.0, 1.0)

    println("Длина первого вектора: ${firstVector.length()}")
    println("Обычный метод: ${firstVector.dotProduct(secondVector)}")
    println("Infix-вызов: ${firstVector dot secondVector}")
    println("Оператор *: ${firstVector * secondVector}")
    println("Внешняя функция: ${scalarProduct(firstVector, secondVector)}")
}
