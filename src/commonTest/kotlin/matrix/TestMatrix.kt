package artemiev.ocrmcproto

import matrix.*
import kotlin.test.Test
import kotlin.test.assertEquals


class TestMatrix {
    @Test
    fun testDoubleMatrix() {
        var m = Matrix(4, 5)
        assertEquals(4, m.rows)
        assertEquals(5, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)

        var s = "1.5"
        m = Matrix(s)
        assertEquals(1, m.rows)
        assertEquals(1, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])

        var a = m.a[0,0] - 1.0
        assertEquals(0.5, a)

        s = "1.5 2.5"
        m = Matrix(s)
        assertEquals(1, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])

        s = "1.5\n" +
                "2.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(1, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[1, 0])

        s = "1.5\t2.5\n" +
                "3.5\t4.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[1, 0])
        assertEquals(4.5, m.a[1, 1])

        s = "1.5 2.5\n" +
                "3.5\t4.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[1, 0])
        assertEquals(4.5, m.a[1, 1])

        s = "-1.5 2.5\n" +
                "0.5\t-0.5"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(2, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(-1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(0.5, m.a[1, 0])
        assertEquals(-0.5, m.a[1, 1])

        s = "1.5 2.5\t3.5 4.5\n" +
                "5.5\t6.5 7.5\t8.5\n"
        m = Matrix(s)
        assertEquals(2, m.rows)
        assertEquals(4, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[0, 2])
        assertEquals(4.5, m.a[0, 3])
        assertEquals(5.5, m.a[1, 0])
        assertEquals(6.5, m.a[1, 1])
        assertEquals(7.5, m.a[1, 2])
        assertEquals(8.5, m.a[1, 3])

        s = "1.5 2.5\t3.5 4.5\n" +
                "5.5\t6.5 7.5\t8.5\n" +
                "9.5 10 -2 0 "
        m = Matrix(s)
        assertEquals(3, m.rows)
        assertEquals(4, m.cols)
        assertEquals(MatrixMode.mDouble, m.mode)
        assertEquals(1.5, m.a[0, 0])
        assertEquals(2.5, m.a[0, 1])
        assertEquals(3.5, m.a[0, 2])
        assertEquals(4.5, m.a[0, 3])
        assertEquals(5.5, m.a[1, 0])
        assertEquals(6.5, m.a[1, 1])
        assertEquals(7.5, m.a[1, 2])
        assertEquals(8.5, m.a[1, 3])
        assertEquals(9.5, m.a[2, 0])
        assertEquals(10.0,m.a[2, 1])
        assertEquals(-2.0,m.a[2, 2])
        assertEquals(0.0, m.a[2, 3])

        s = "1 2\t \n" +
                "3 4 \n" +
                "5 6\t \n\n\n"
        m = Matrix(s)
        assertEquals(3, m.rows)
        assertEquals(2, m.cols)

        var m1 = m - m
        assertEquals(3, m1.rows)
        assertEquals(2, m1.cols)
        assertEquals(0.0, m1.a[0, 0])
        assertEquals(0.0, m1.a[2, 1])
        assertEquals(1.0, m.a[0, 0])
        assertEquals(6.0, m.a[2, 1])

        m1 = m - m * 2.0
        assertEquals(3, m1.rows)
        assertEquals(2, m1.cols)
        assertEquals(-1.0, m1.a[0, 0])
        assertEquals(-6.0, m1.a[2, 1])
        assertEquals(1.0, m.a[0, 0])
        assertEquals(6.0, m.a[2, 1])

        m1 = m1 + 1.0
        assertEquals(3, m1.rows)
        assertEquals(2, m1.cols)
        assertEquals(0.0, m1.a[0, 0])
        assertEquals(-5.0, m1.a[2, 1])

        assertEquals(m, m + 0.0)
        assertEquals(m + 1.0, 1.0 + m)
        assertEquals(m1 * 2.0, 2.0 * m1)

        var I = identity(3, MatrixMode.mDouble)
        assertEquals(3, I.rows)
        assertEquals(3, I.cols)
        assertEquals(1.0, I[0, 0])
        assertEquals(1.0, I[1, 1])
        assertEquals(1.0, I[2, 2])
        assertEquals(0.0, I[0, 1])
        assertEquals(0.0, I[0, 2])
        assertEquals(0.0, I[2, 1])
        assertEquals(m, I * m)

        assertEquals(m, m * identity(2, MatrixMode.mDouble))

        m = Matrix("1 2 3\n4 5 6")
        m1 = Matrix("1 2\n3 4\n5 6")
        var m2 = Matrix("22 28\n49 64")
        assertEquals(m2, m * m1)

        m = Matrix("-1\n4\n0")
        m1 = Matrix("1 2 -3")
        m2 = Matrix("-1 -2 3\n4 8 -12\n0 0 0")
        assertEquals(m2, m * m1)

        m1 = Matrix(m2.toString())
        assertEquals(m2, m1)
    }

    @Test
    fun testFractionMatrix() {
        var m = Matrix("1/1 2/1\n3/1 4/1")
        var I = identity(2, MatrixMode.mFraction)
        assertEquals("1/1 0/1\n0/1 1/1", I.toString())
        assertEquals(m, m * I)

        m = Matrix("1/2 2/3\n3/4 4/5")
        assertEquals(m, m * I)

        m = Matrix("1/2 2/3 3/4\n4/5 5/6 6/7")
        var m1 = Matrix(m.toString())
        assertEquals(m, m1)

        I = identity(3, MatrixMode.mFraction)
        assertEquals("1/1 0/1 0/1\n0/1 1/1 0/1\n0/1 0/1 1/1", I.toString())
        assertEquals(m, m * I)
    }
}
