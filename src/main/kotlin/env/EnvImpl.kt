package env

import Expr
import RuntimeSymbol

class EnvImpl(
    override val parent: Env? = null,
    private val symbols: MutableMap<RuntimeSymbol, Expr> = hashMapOf()
) : Env {
    override operator fun get(symbol: RuntimeSymbol): Expr? {
        return symbols[symbol] ?: parent?.get(symbol)
    }

    override operator fun set(runtimeSymbol: RuntimeSymbol, global: Boolean, expr: Expr) {
        if (global && parent != null) {
            parent[runtimeSymbol, global] = expr
        } else {
            symbols[runtimeSymbol] = expr
        }
    }
}
