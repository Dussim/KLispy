package expr

import asserts.mapErrorMessage
import datastructures.L
import datastructures.Right
import datastructures.Some
import datastructures.find
import datastructures.forEach
import datastructures.iterator
import datastructures.joinToString
import datastructures.map
import datastructures.merge
import env.Env
import env.get
import env.set
import env.subEnv
import iterators.toL
import iterators.zip

data class LambdaExpr(val symbols: L<SymbolExpr.Unbound>, val body: QExpr) : Expr {
    override fun eval(env: Env): Expr = this

    override fun eval(env: Env, l: L<Expr>): Expr {
        val innerEnv = env.subEnv()
        val setSymbol: (Pair<SymbolExpr.Unbound, Expr>) -> Unit = { (symbol, expr) ->
            innerEnv.set(symbol.bindTo(expr))
        }
        val symbolsIterator = symbols.iterator()
        val valuesIterator = l.iterator()
        return when (symbols.find { it.symbol == ":" }) {
            is Some -> prepareVarargs(symbols, l).mapErrorMessage { "Curried varargs are not supported, $it" }
            else -> Right(symbolsIterator.zip(valuesIterator, ::Pair))
        }.map {
            it.forEach(setSymbol)
            when (symbolsIterator.hasNext()) {
                true -> LambdaExpr(symbolsIterator.toL(), body.deepConstitution(innerEnv) as QExpr)
                false -> SExpr(body.content).eval(innerEnv)
            }
        }.merge()
    }

    override fun toString(): String = "\\ ${symbols.joinToString("{", "}")} $body"
}

fun Expr.deepConstitution(env: Env): Expr {
    return when (this) {
        is QExpr -> QExpr(
            content.map { it.deepConstitution(env) }
        )
        is SExpr -> SExpr(
            content.map { it.deepConstitution(env) }
        )
        else -> env[this, false] ?: this
    }
}
