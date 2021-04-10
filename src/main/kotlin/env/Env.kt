package env

import datastructures.L
import datastructures.forEach
import datastructures.of
import equal
import expr.Expr
import expr.False
import expr.SymbolExpr
import expr.True
import expr.`if`
import expr.assign
import expr.bindTo
import expr.def
import expr.divide
import expr.eval
import expr.head
import expr.join
import expr.list
import expr.makeLambda
import expr.minus
import expr.multiply
import expr.plus
import expr.tail
import greaterEqual
import greaterThan
import lessEqual
import lessThan
import notEqual

interface Env {
    val parent: Env?

    operator fun get(symbol: String, global: Boolean = true): Expr?
    operator fun set(symbol: String, global: Boolean = false, expr: Expr)
}

fun Env.set(symbolExpr: SymbolExpr.Builtin, global: Boolean = false) = this.set(symbolExpr.symbol, global, symbolExpr)
fun Env.set(symbolExpr: SymbolExpr.Bound, global: Boolean = false) = this.set(symbolExpr.symbol, global, symbolExpr.expr)

fun Env.subEnv(): Env = EnvImpl(this)

fun Env.addBuiltIns() {
    L.of(
        plus,
        minus,
        divide,
        multiply,
        list,
        head,
        tail,
        eval,
        join,
        def,
        assign,
        makeLambda,
        greaterThan,
        lessThan,
        greaterEqual,
        lessEqual,
        equal,
        notEqual,
        `if`
    ).forEach(::set)
    set(SymbolExpr.Unbound("true").bindTo(True))
    set(SymbolExpr.Unbound("false").bindTo(False))
}

operator fun Env.get(expr: Expr, global: Boolean = true): Expr? = when (expr) {
    is SymbolExpr -> get(expr.symbol, global)
    else -> null
}
