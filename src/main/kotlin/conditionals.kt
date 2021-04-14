import asserts.assertTwoExprs
import asserts.assertTypes
import datastructures.L
import datastructures.flatMap
import datastructures.map
import datastructures.mapLeft
import datastructures.merge
import expr.ErrorExpr
import expr.Expr
import expr.False
import expr.NumberExpr
import expr.SymbolExpr
import expr.True

private fun implementedFor(symbol: String, a: Expr, b: Expr) = "( $symbol NumberExpr NumberExpr ) but got ( $symbol ${a.typeName} ${b.typeName} )"

private fun Boolean.toExpr(): Expr = if (this) True else False

private fun order(symbol: String, l: L<Expr>, compare: (NumberExpr, NumberExpr) -> Expr): Expr {
    return l.assertTwoExprs()
        .flatMap { (a, b) ->
            assertTypes<NumberExpr, NumberExpr>(a, b)
                .mapLeft { ErrorExpr("$symbol is currently only implemented for ${implementedFor(symbol, a, b)}") }
        }
        .map { (a, b) -> compare(a, b) }
        .merge()
}

private fun comparison(symbol: String, compare: (NumberExpr, NumberExpr) -> Boolean): SymbolExpr.Builtin = SymbolExpr.Builtin(symbol) { _, l -> order(symbol, l) { a, b -> compare(a, b).toExpr() } }

val Expr.typeName get() = this::class.simpleName

val greaterThan = comparison("<") { a, b -> a < b }
val lessThan = comparison(">") { a, b -> a > b }
val greaterEqual = comparison("<=") { a, b -> a <= b }
val lessEqual = comparison(">=") { a, b -> a >= b }

private fun eq(l: L<Expr>, f: (Expr, Expr) -> Boolean): Expr {
    return l.assertTwoExprs()
        .map { (a, b) -> f(a, b).toExpr() }
        .merge()
}

val equal = SymbolExpr.Builtin("==") { _, l -> eq(l) { a, b -> a.eq(b) } }
val notEqual = SymbolExpr.Builtin("!=") { _, l -> eq(l) { a, b -> !a.eq(b) } }
