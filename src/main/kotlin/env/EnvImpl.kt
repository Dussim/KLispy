package env

import Expr

class EnvImpl(
    override val parent: Env? = null,
    private val symbols: MutableMap<String, Expr> = hashMapOf()
) : Env {
    override operator fun get(symbol: String, global: Boolean): Expr? {
        return if (global) {
            symbols[symbol] ?: parent?.get(symbol)
        } else {
            symbols[symbol]
        }
    }

    override operator fun set(symbol: String, global: Boolean, expr: Expr) {
        if (global && parent != null) {
            parent[symbol, global] = expr
        } else {
            symbols[symbol] = expr
        }
    }
}
