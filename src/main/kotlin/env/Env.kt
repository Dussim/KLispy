package env

import Expr
import Symbol
import datastructures.L
import datastructures.forEach
import datastructures.of
import def
import divide
import eval
import head
import join
import lambda
import list
import minus
import multiply
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
    L.of(plus, minus, divide, multiply, list, head, tail, eval, join, def, lambda)
        .forEach(::set)
}

operator fun Env.get(expr: Expr, global: Boolean = true): Expr? = when (expr) {
    is Symbol -> get(expr.symbol, global)
    else -> null
}
