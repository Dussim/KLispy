package env

import Expr
import RuntimeSymbol

interface Env {
    val parent: Env?

    operator fun get(symbol: RuntimeSymbol): Expr?

    operator fun set(runtimeSymbol: RuntimeSymbol, global: Boolean = false, expr: Expr)

    companion object
}

fun Env.subEnv() = EnvImpl(this)
