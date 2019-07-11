package matrix

operator fun Number.unaryMinus(): Number {
    if (this is Fraction)
        return - this

    if (this is Double)
        return - this

    throw Exception("Not implemented")
}

operator fun Number.minus(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this - a

    if (this is Double && a is Double)
        return this - a

    throw Exception("Not implemented")
}

operator fun Number.minus(a: Matrix): Matrix {
    return  - a + this
}

operator fun Number.plus(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this + a

    if (this is Double && a is Double)
        return this + a

    throw Exception("Not implemented")
}

operator fun Number.plus(a: Matrix): Matrix {
    return  a + this
}

operator fun Number.times(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this * a

    if (this is Double && a is Double)
        return this * a

    throw Exception("Not implemented")
}

operator fun Number.times(a: Matrix): Matrix {
    return a * this
}

operator fun Number.div(a: Number): Number {
    if (this is Fraction && a is Fraction)
        return this / a

    if (this is Double && a is Double)
        return this / a

    throw Exception("Not implemented")
}