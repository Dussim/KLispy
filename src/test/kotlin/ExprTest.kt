import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.math.pow
import kotlin.test.assertEquals

internal class ExprTest {
    @TestFactory
    fun `given list of numbers when '+' operator is evaluated then correct number is returned`() =
        (1..20).toList()
            .map(::generateNFirstNumbersTestCase)
            .map { (list, expected) ->
                L.of(*list.toTypedArray())
                    .map(Int::toDouble compose ::Number) to expected
            }.map { (l, expected) ->
                val listAsString = l.joinToString("(", ")") { it.value.toString() }
                dynamicTest(
                    "given list of numbers $listAsString when '+' operator is evaluated then correct value is returned $expected"
                ) {
                    assertEquals(expected, Plus.eval(l))
                }
            }.stream()

    @TestFactory
    fun `given empty list of numbers, when operators are evaluated then error is returned`() =
        listOf(Plus, Minus, Multiply, Divide)
            .map { symbol ->
                dynamicTest(
                    "given empty list, when operator '$symbol' is evaluated then error is returned"
                ) {
                    assertEquals(Error.emptyParamListForSymbol(symbol), symbol.eval(L.empty()))
                }
            }

    @TestFactory
    fun `given non-numbers list, when operators are evaluated then error is returned`() =
        listOf(Plus, Minus, Multiply, Divide)
            .map { symbol ->
                dynamicTest(
                    "given non-numbers list, when operator '$symbol' is evaluated then error is returned"
                ) {
                    val l = L.of(Error(reason = "This is not a number"))

                    assertEquals(Error.paramsNotNumbers(symbol, l), symbol.eval(l))
                }
            }

    private fun generateNFirstNumbersTestCase(n: Int): Pair<List<Int>, Number> {
        return List(n) { it + 1 } to Number((n.toDouble().pow(2.0) + n) / 2.0)
    }
}
