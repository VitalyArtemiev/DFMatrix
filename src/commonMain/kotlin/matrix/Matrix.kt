package matrix

import kotlin.math.pow

enum class MatrixMode { mDouble, mFraction }

class Matrix {
    var a: Array2D
    var mode: MatrixMode
    var rows: Int
    var cols: Int

    constructor(r: Int, c: Int, m: MatrixMode = MatrixMode.mDouble) {
        rows = r
        cols = c
        mode = m

        when (mode) {
            MatrixMode.mDouble -> {
                a = Array2DDouble(rows, cols)
            }
            MatrixMode.mFraction -> {
                a = Array2DFraction(rows, cols)
            }
        }
    }

    constructor(s: String) {
        val lines = s.dropLastWhile { it == '\n' }.split("\n")
        rows = lines.size
        cols = lines[0].dropLastWhile { it == '\t' || it == ' ' }.split(" ", "\t").size

        if (s.contains("/")) {
            mode = MatrixMode.mFraction
            a = Array2DFraction(rows, cols)

            for ((i, line) in lines.withIndex()) {
                val elements = line.dropLastWhile { it == '\t' || it == ' '}.split(" ", "\t")

                if (elements.size != cols)
                    throw Exception("Invalid matrix: malformed columns")

                for ((j, element) in elements.withIndex()) {
                    a[i, j] = Fraction.valueOf(element)
                }
            }
        } else {
            mode = MatrixMode.mDouble
            a = Array2DDouble(rows, cols)

            for ((i, line) in lines.withIndex()) {
                val elements = line.dropLastWhile { it == '\t' || it == ' '}.split(" ", "\t")

                if (elements.size != cols)
                    throw Exception("Invalid matrix: malformed columns")

                for ((j, element) in elements.withIndex()) {
                    a[i, j] = element.toDouble()
                }
            }
        }
    }

    override operator fun equals(other: Any?): Boolean {
        return if (other is Matrix) {
            if (other.rows != rows || other.cols != cols)
                return false

            for (i in 0 until rows){
                for (j in 0 until cols) {
                    if (other.a[i,j] != a[i,j]){
                        return false
                    }
                }
            }
            true
        }
        else
            false
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    operator fun get(i: Int): Any {
        return a[i]
    }

    operator fun get(i: Int, j: Int): Number {
        return a[i, j]
    }

    operator fun set(i: Int, j: Int, v: Any) {
        a[i, j] = v as Number
    }

    fun swapRow(r1: Int, r2: Int) {
        val t = a[r2]
        a[r2] = a[r1]
        a[r1] = t
        /*for i= 0 to Col - 1 do
  a[r1, i]*= -1; //change sign so to not change det  */
    }

    fun copy(): Matrix {
        val result = Matrix(rows, cols, mode)

        result.a = a.copy()
        return result
    }

    override fun toString(): String {
        var s = ""

        for (i in 0 until rows) {
            for (j in 0 until cols)
                s += a[i, j].toString() + ' '
            s = s.dropLast(1)

            if (i != rows - 1)
                s += '\n'.toString()
        }
        return s
    }

    fun toFractionMatrix(): Matrix {
        if (mode == MatrixMode.mFraction)
            return copy()

        val fm = Matrix(rows, cols, MatrixMode.mFraction)

        for (i in 0 until rows)
            for (j in 0 until cols) {
                val iPart = (a[i, j] as Double).toInt()
                var fPart = a[i, j] as Double - iPart//todo: write own number operator?
                if (fPart == 0.0) {
                    fm.a[i,j] = Fraction(iPart, 1)
                } else {
                    var k = 0
                    while (fPart != fPart.toInt().toDouble()) {
                        fPart *= 10
                        k++
                    }

                    val den: Int = 10.0.pow(k).toInt()
                    val num = fPart.toInt() + den * iPart

                    fm.a[i,j] = Fraction(num, den)
                }
            }
        return fm
    }

    operator fun unaryMinus(): Matrix {
        val result = Matrix(rows, cols, mode)

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result.a[i, j] = -a[i, j]
            }
        }
        return result
    }

    operator fun plus(m: Matrix): Matrix {
        require(rows == m.rows && cols == m.cols) { "Matrix size mismatch during sum" }

        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] += m[i, j]
            }
        return result
    }

    operator fun plus(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] += n
            }
        return result
    }

    operator fun minus(m: Matrix): Matrix {
        require(rows == m.rows && cols == m.cols) { "Matrix size mismatch during sum" }

        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] -= m[i, j]
            }
        return result
    }

    operator fun minus(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] -= n
            }
        return result
    }

    operator fun times(m: Matrix): Matrix {
        require(cols == m.rows) { "Matrix dimension mismatch" }

        val result = Matrix(rows, m.cols, mode)

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result[i, j] = initNumber()
                for (k in 0 until cols) {
                    result[i, j] += a[i, k] * m[k, j]
                }
            }
        }

        return result
    }

    operator fun times(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols)
                result.a[i, j] *= n

        return result
    }

    fun initNumber(): Number {
        return when (mode){
            MatrixMode.mDouble -> {
                0.0 //as Double - no cast needed
            }
            MatrixMode.mFraction -> {
                Fraction()
            }
        }
    }

    fun initNumber(v: Int): Number {
        return when (mode) {
            MatrixMode.mDouble -> {
                v.toDouble() //as Double - no cast needed
            }
            MatrixMode.mFraction -> {
                Fraction(v, 1)
            }
        }
    }

    fun LUDecompose(): Pair<Matrix, Matrix> {
        val u = Matrix(rows, cols, mode)
        val l = Matrix(rows, cols, mode)

        for (i in 0 until l.rows) {
            u.a[0, i] = a[0, i]
            l.a[i, i] = initNumber(1)
        }

        for (i in 0 until u.rows) {
            for (j in 0 until u.rows) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += l.a[i,k] * u.a[k,j]
                }
                u.a[i, j] = a[i, j] - s
            }

            for (j in i + 1 until u.rows) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += l.a[j,k] * u.a[k,i]
                }

                if (!u.a[i, i].isZero())
                    l.a[j, i] = (a[j, i] - s) / u.a[i, i]
                else
                    l.a[j, i] = initNumber(1) //???
            }
        }

        return Pair(l, u)
    }

    fun det(): Number {
        check(cols == rows) { "Square matrix required" }
        val a: Matrix = copy()

        for (i in 0 until a.cols)
        //this ignores extra rows
        {
            for (j in i until a.cols) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += a.a[i,k] * a.a[k,j]
                }
                a.a[i,j] = a.a[i,j] - s
            }

            for (j in i + 1 until a.cols) {
                var s = initNumber()
                for (k in 0 until i) {
                    s += a.a[j,k] * a.a[k,i]
                }

                if (!a.a[i, i].isZero())
                    a.a[j,i] = (a.a[j,i] - s) / a.a[i,i]
                else
                    return initNumber()
            }
        }

        var result = initNumber(1)

        for (i in 0 until a.cols) {
            result *= a.a[i,i]
        }

        if (result == -0.0) //apparently -0.0 != +0.0 but only sometimes
            result = 0.0

        return result
    }

    fun transpose(): Matrix {
        val result = Matrix(cols, rows)

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result.a[i, j] = a[j, i]
            }
        }
        return result
    }
}

fun identity(size: Int, mode: MatrixMode): Matrix {
    val m = Matrix(size, size, mode)
    when (mode) {
        MatrixMode.mDouble -> for (i in 0 until size) {
            m[i, i] = 1.0
        }
        MatrixMode.mFraction -> for (i in 0 until size) {
            m[i, i] = Fraction(1, 1)
        }
    }
    return m
}