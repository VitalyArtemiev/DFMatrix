package matrix

import kotlin.math.pow

//import android.widget.EditText

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
        val a = a[r2]
        this.a[r2] = this.a[r1]
        this.a[r1] = a
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

    fun getFractionMatrix(m: Matrix): Matrix {
        if (mode == MatrixMode.mFraction)
            return this.copy()

        val fm = Matrix(m.rows, m.cols, MatrixMode.mFraction)
        val rm = m //as RealMatrix

        for (i in 0 until m.rows)
            for (j in 0 until m.cols) {
                val iPart = (rm.a[i,j] as Double).toInt()
                var fPart = rm.a[i,j] as Double - iPart//todo: write own number operator?
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
        return invert()
    }

    operator fun plus(m: Matrix): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] = a[i, j] + m[i,j]
            }
        return result
    }

    operator fun plus(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] = a[i, j] + n
            }
        return result
    }

    operator fun minus(m: Matrix): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] = a[i, j] - m[i,j]
            }
        return result
    }

    operator fun minus(n: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols) {
                result.a[i, j] = a[i, j] + n
            }
        return result
    }

    operator fun times(m: Matrix): Matrix {
        if (cols != m.rows)
            throw Exception("Matrix dimension mismatch")

        val result = Matrix(rows, m.cols, mode)

        var k: Int

        for (i in 0 until result.rows) {
            for (j in 0 until result.cols) {
                result[i, j] = initNumber()
                k = 0
                while (k < cols) {
                    result[i, j] += a[i, k] * m[k, j]
                    k++
                }
            }
        }

        return result
    }

    operator fun times(m: Number): Matrix {
        val result = copy()

        for (i in 0 until rows)
            for (j in 0 until cols)
                result.a[i,j] = a[i,j] * m

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

    fun LUDecompose(a: Matrix): Pair<Matrix, Matrix> {
        val u = Matrix(a.rows, a.cols, a.mode)
        val l = Matrix(a.rows, a.cols, a.mode)

        var i: Int = 0
        var j: Int
        var k: Int

        while (i < l.rows) {
            u.a[0,i] = a.a[0,i]
            l.a[i,i] = 1.0
            i++
        }

        i = 0
        while (i < u.rows) {
            j = i
            while (j < u.rows) {
                var s = initNumber()
                k = 0
                while (k < i) {
                    s += l.a[i,k] * u.a[k,j]
                    k++
                }
                u.a[i,j] = a.a[i,j] - s
                j++
            }

            j = i + 1
            while (j < u.rows) {
                var s = initNumber()
                k = 0
                while (k < i) {
                    s += l.a[j,k] * u.a[k,i]
                    k++
                }

                if (u.a[i,i] != 0.0)
                    l.a[j,i] = (a.a[j,i] - s) / u.a[i,i]
                else
                    l.a[j,i] = 1.0 //???
                j++
            }
            i++
        }

        return Pair(u, l)
    }

    fun det(): Number {
        val a: Matrix = this.copy()
        var i: Int = 0
        var j: Int
        var k: Int

        while (i < a.cols)
        //this ignores extra rows
        {
            j = i
            while (j < a.cols) {
                var s = initNumber()
                k = 0
                while (k < i) {
                    s += a.a[i,k] * a.a[k,j]
                    k++
                }
                a.a[i,j] = a.a[i,j] - s
                j++
            }

            j = i + 1
            while (j < a.cols) {
                var s = initNumber()
                k = 0
                while (k < i) {
                    s += a.a[j,k] * a.a[k,i]
                    k++
                }

                if (a.a[i,i] != 0.0)
                    a.a[j,i] = (a.a[j,i] - s) / a.a[i,i]
                else
                    return 0.toDouble()
                j++
            }
            i++
        }

        var result = initNumber()

        i = 0
        while (i < a.cols) {
            result *= a.a[i,i]
            i++
        }
        return result
    }

    fun invert(): Matrix {
        val result = Matrix(this.rows, this.cols)
        var i: Int = 0
        var j: Int

        while (i < result.rows) {
            j = 0
            while (j < result.cols) {
                result.a[i,j] = -this.a[i,j]
                j++
            }

            i++
        }
        return result
    }

    fun transpose(): Matrix {
        val r = Matrix(this.cols, this.rows)

        var i = 0
        var j: Int

        while (i < r.rows) {

            j = 0
            while (j < r.cols) {
                r.a[i,j] = this.a[j,i]
                j++
            }

            i++
        }
        return r
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