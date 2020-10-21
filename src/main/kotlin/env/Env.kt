package env

import Expr
import False
import Symbol
import True
import `if`
import assign
import bindTo
import datastructures.L
import datastructures.forEach
import datastructures.of
import def
import divide
import equal
import eval
import greaterEqual
import greaterThan
import head
import join
import lambda
import lessEqual
import lessThan
import list
import minus
import multiply
import notEqual
import plus
import tail

interface Env {
    val parent: Env?

    operator fun get(symbol: String, global: Boolean = true): Expr?
    operator fun set(symbol: String, global: Boolean = false, expr: Expr)
}

fun Env.set(symbol: Symbol.Builtin, global: Boolean = false) = this.set(symbol.symbol, global, symbol)
fun Env.set(symbol: Symbol.Bound, global: Boolean = false) = this.set(symbol.symbol, global, symbol.expr)

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
        lambda,
        greaterThan,
        lessThan,
        greaterEqual,
        lessEqual,
        equal,
        notEqual,
        `if`
    ).forEach(::set)
    set(Symbol.Unbound("true").bindTo(True))
    set(Symbol.Unbound("false").bindTo(False))
}

operator fun Env.get(expr: Expr, global: Boolean = true): Expr? = when (expr) {
    is Symbol -> get(expr.symbol, global)
    else -> null
}
