package env

import Expr

class EnvImpl(
    override val parent: Env? = null,
    private val symbols: MutableMap<String, Expr> = hashMapOf()
) : Env {
    override operator fun get(symbol: String): Expr? {
        return symbols[symbol] ?: parent?.get(symbol)
    }

    override fun add(symbol: String, expr: Expr, global: Boolean) {
        if (global && parent != null) {
            parent.add(symbol, expr, global)
        } else {
            symbols[symbol] = expr
        }
    }
}
