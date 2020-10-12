package env

import Expr
import RuntimeSymbol

interface Env {
    operator fun get(symbol: RuntimeSymbol): Expr?

    fun add(runtimeSymbol: RuntimeSymbol, expr: Expr)
}
