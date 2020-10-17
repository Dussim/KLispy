package env

import Expr
import RuntimeSymbol

object EmptyEnv : Env {
    override val parent: Env? = null

    override fun get(symbol: RuntimeSymbol): Expr? = null

    override fun set(runtimeSymbol: RuntimeSymbol, global: Boolean, expr: Expr) = Unit
}
