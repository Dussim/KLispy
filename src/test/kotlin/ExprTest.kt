import datastructures.L
import datastructures.empty
import datastructures.joinToString
import datastructures.map
import datastructures.of
import env.Env
import env.EnvImpl
import expr.ErrorExpr
import expr.NumberExpr
import expr.divide
import expr.minus
import expr.multiply
import expr.plus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import utils.compose
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ExprTest {
    lateinit var env: Env

    @BeforeEach
    fun beforeEach() {
        env = EnvImpl()
    }

    @TestFactory
    fun `given list of numbers when '+' operator is evaluated then correct number is returned`() =
        (1..20).toList()
            .map(::generateNFirstNumbersTestCase)
            .map { (list, expected) ->
                L.of(*list.toTypedArray())
                    .map(Int::toDouble compose ::NumberExpr) to expected
            }.map { (l, expected) ->
                val listAsString = l.joinToString("(", ")") { it.value.toString() }
                dynamicTest(
                    "given list of numbers $listAsString when '+' operator is evaluated then correct value is returned $expected"
                ) {
                    assertEquals(expected, plus.eval(env, l))
                }
            }.stream()

    @TestFactory
    fun `given empty list of numbers, when operators are evaluated then error is returned`() =
        listOf(plus, minus, multiply, divide)
            .map { symbol ->
                dynamicTest(
                    "given datastructures.empty list, when operator '$symbol' is evaluated then error is returned"
                ) {
                    assertTrue {
                        symbol.eval(env, L.empty()) is ErrorExpr
                    }
                }
            }

    @TestFactory
    fun `given non-numbers list, when operators are evaluated then error is returned`() =
        listOf(plus, minus, multiply, divide)
            .map { symbol ->
                dynamicTest(
                    "given non-numbers list, when operator '$symbol' is evaluated then error is returned"
                ) {
                    val l = L.of(ErrorExpr(reason = "This is not a number"))
                    assertTrue {
                        symbol.eval(env, l) is ErrorExpr
                    }
                }
            }

    private fun generateNFirstNumbersTestCase(n: Int): Pair<List<Int>, NumberExpr> {
        return List(n) { it + 1 } to NumberExpr((n.toDouble().pow(2.0) + n) / 2.0)
    }
}
