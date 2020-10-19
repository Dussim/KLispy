import asserts.checkTypes
import asserts.exactly2
import datastructures.L

private fun implementedFor(symbol: String, a: Expr, b: Expr) = "( $symbol Number Number ) but got ( $symbol ${a.typeName} ${b.typeName} )"

private fun Boolean.toExpr(): Expr = if (this) True else False

private fun order(l: L<Expr>, compare: (Number, Number) -> Expr): Expr {
    return l.exactly2 { a, b ->
        checkTypes<Number, Number>(a, b) { n1, n2 -> compare(n1, n2) }
    }
}

private fun comparison(symbol: String, compare: (Number, Number) -> Boolean): Symbol.Builtin = Symbol.Builtin(symbol) { _, l -> order(l) { a, b -> compare(a, b).toExpr() } }

private val Expr.typeName get() = this::class.simpleName

val greaterThan = comparison("<") { a, b -> a < b }
val lessThan = comparison(">") { a, b -> a > b }
val greaterEqual = comparison("<=") { a, b -> a <= b }
val lessEqual = comparison(">=") { a, b -> a >= b }

val equal = Symbol.Builtin("==") { env, l ->
    l.exactly2 { a, b ->
        checkTypes<Number, Number>(a, b) { n1, n2 -> return@Builtin (n1.value == n2.value).toExpr() }
        ErrorExpr("Equality is currently only implemented for ${implementedFor("==", a, b)}")
    }
}
val notEqual = Symbol.Builtin("!=") { env, l ->
    l.exactly2 { a, b ->
        checkTypes<Number, Number>(a, b) { n1, n2 -> return@Builtin (n1.value != n2.value).toExpr() }
        ErrorExpr("Inequality is currently only implemented for ${implementedFor("!=", a, b)}")
    }
}
