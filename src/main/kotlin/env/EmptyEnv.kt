package env

import Expr
import RuntimeSymbol

object EmptyEnv : Env {
    override fun get(symbol: RuntimeSymbol): Expr? = null

    override fun add(runtimeSymbol: RuntimeSymbol, expr: Expr) = Unit
}
