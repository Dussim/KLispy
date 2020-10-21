import asserts.assertTwoExprs
import asserts.assertTypes
import datastructures.L
import datastructures.flatMap
import datastructures.map
import datastructures.mapLeft
import datastructures.merge

private fun implementedFor(symbol: String, a: Expr, b: Expr) = "( $symbol Number Number ) but got ( $symbol ${a.typeName} ${b.typeName} )"

private fun Boolean.toExpr(): Expr = if (this) True else False

private fun order(l: L<Expr>, compare: (Number, Number) -> Expr): Expr {
    return l.assertTwoExprs()
        .flatMap { (a, b) -> assertTypes<Number, Number>(a, b) }
        .map { (a, b) -> compare(a, b) }
        .merge()
}

private fun comparison(symbol: String, compare: (Number, Number) -> Boolean): Symbol.Builtin = Symbol.Builtin(symbol) { _, l -> order(l) { a, b -> compare(a, b).toExpr() } }

val Expr.typeName get() = this::class.simpleName

val greaterThan = comparison("<") { a, b -> a < b }
val lessThan = comparison(">") { a, b -> a > b }
val greaterEqual = comparison("<=") { a, b -> a <= b }
val lessEqual = comparison(">=") { a, b -> a >= b }

private fun eq(op: String, l: L<Expr>, f: (Double, Double) -> Boolean): Expr {
    return l.assertTwoExprs()
        .flatMap { (a, b) ->
            assertTypes<Number, Number>(a, b)
                .mapLeft { ErrorExpr("Equality is currently only implemented for ${implementedFor(op, a, b)}") }
        }
        .map { (a, b) -> Pair(a.value, b.value) }
        .map { (a, b) -> f(a, b).toExpr() }
        .merge()
}

val equal = Symbol.Builtin("==") { env, l ->
    eq("==", l) { a, b -> a == b }
}
val notEqual = Symbol.Builtin("!=") { env, l ->
    eq("!=", l) { a, b -> a != b }
}
