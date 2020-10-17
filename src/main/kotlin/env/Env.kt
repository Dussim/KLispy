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

    operator fun get(symbol: String): Expr?

    fun add(symbol: String, expr: Expr, global: Boolean = false)

    companion object
}

fun Env.add(symbol: Symbol.Builtin, global: Boolean = false) = add(symbol.symbol, symbol, global)
fun Env.add(symbol: Symbol.Bound, global: Boolean = false) = add(symbol.symbol, symbol.expr, global)

fun Env.subEnv() = EnvImpl(this)

fun Env.addBuiltIns() {
    L.of(plus, minus, divide, multiply, list, head, tail, eval, join, def, lambda)
        .forEach(::add)
}
