package env

import Expr
import RuntimeSymbol

class EnvImpl(private val symbols: MutableMap<RuntimeSymbol, Expr> = hashMapOf()) : Env {
    override operator fun get(symbol: RuntimeSymbol): Expr? {
        return symbols[symbol]
    }

    override fun add(runtimeSymbol: RuntimeSymbol, expr: Expr) {
        symbols[runtimeSymbol] = expr
    }
}
