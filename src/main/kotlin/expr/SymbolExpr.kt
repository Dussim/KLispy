package expr

import asserts.allOfType
import asserts.assertCons
import asserts.assertOneExpr
import asserts.assertThreeExprs
import asserts.assertTwoExprs
import asserts.assertType
import asserts.assertTypes
import datastructures.Cons
import datastructures.L
import datastructures.Right
import datastructures.Some
import datastructures.find
import datastructures.flatMap
import datastructures.flatten
import datastructures.forEach
import datastructures.map
import datastructures.merge
import env.Env
import env.set

sealed class SymbolExpr : Expr {
    abstract val symbol: String

    data class Unbound(override val symbol: String) : SymbolExpr() {
        override fun toString(): String = symbol
        override fun eval(env: Env): Expr = env[symbol] ?: ErrorExpr("Symbol [ $symbol ] not found")
        override fun eval(env: Env, l: L<Expr>): Expr = env[symbol]?.eval(env, l) ?: ErrorExpr("Symbol [ $symbol ] not found")
    }

    data class Builtin(override val symbol: String, val function: SymbolExpr.(env: Env, l: L<Expr>) -> Expr) : SymbolExpr() {
        override fun toString(): String = symbol
        override fun eval(env: Env): Expr = this
        override fun eval(env: Env, l: L<Expr>): Expr = function(env, l)
    }

    data class Bound(override val symbol: String, val expr: Expr) : SymbolExpr() {
        override fun toString(): String = expr.toString()
        override fun eval(env: Env): Expr = expr
        override fun eval(env: Env, l: L<Expr>): Expr = expr.eval(env, l)
    }
}

fun SymbolExpr.Unbound.bindTo(expr: Expr): SymbolExpr.Bound = SymbolExpr.Bound(symbol, expr)

// built-ins

val list = SymbolExpr.Builtin("list") { _, l -> QExpr(l) }
val head = SymbolExpr.Builtin("head") { _, l -> qExprFun(l) { cons -> QExpr(cons.head) } }
val tail = SymbolExpr.Builtin("tail") { _, l -> qExprFun(l) { cons -> QExpr(cons.tail) } }
val eval = SymbolExpr.Builtin("eval") { env, l -> qExprFun(l) { cons -> SExpr(cons).eval(env) } }
val join = SymbolExpr.Builtin("join") { _, l ->
    Right(l)
        .flatMap { it.assertCons() }
        .flatMap { it.allOfType<QExpr>() }
        .map { it.map(QExpr::content) }
        .map { it.flatten() }
        .map { QExpr(it) }
        .merge()
}

val def = define("def", true)
val assign = define("=", false)

val makeLambda = SymbolExpr.Builtin("\\") { _, l ->
    l.assertTwoExprs()
        .flatMap { (a, b) -> assertTypes<QExpr, QExpr>(a, b) }
        .map { (a, b) -> Pair(a.content, b) }
        .flatMap { (arguments, body) ->
            arguments.allOfType<SymbolExpr.Unbound>()
                .map { symbols -> LambdaExpr(symbols, body) }
        }.merge()
}

val `if` = SymbolExpr.Builtin("if") { env, l ->
    l.assertThreeExprs()
        .flatMap { (a, b, c) -> assertTypes<BooleanExpr, QExpr, QExpr>(a, b, c) }
        .map { (a, b, c) -> Triple(a, b.toSExpr(), c.toSExpr()) }
        .map { (a, b, c) -> if (a is True) b else c }
        .map { it.eval(env) }
        .merge()
}

fun define(op: String, global: Boolean): SymbolExpr.Builtin {
    return SymbolExpr.Builtin(op) { env, l ->
        val setSymbol: (Pair<SymbolExpr.Unbound, Expr>) -> Unit = { (symbol, expr) ->
            env.set(symbol.bindTo(expr), global)
        }

        l.assertCons()
            .flatMap { (a, b) ->
                a.assertType<QExpr>().map { qExpr -> Pair(qExpr, b) }
            }
            .flatMap { (a, b) ->
                a.content.allOfType<SymbolExpr.Unbound>()
                    .map { symbols -> Pair(symbols, b) }
            }
            .flatMap { (symbols, arguments) ->
                when (symbols.find { it.symbol == ":" }) {
                    is Some -> prepareVarargs(symbols, arguments)
                    else -> prepareNormalArgs(symbols, arguments)
                }
            }
            .map { it.forEach(setSymbol).run { SExpr() } }
            .merge()
    }
}

fun qExprFun(l: L<Expr>, f: (Cons<Expr>) -> Expr): Expr = l.assertOneExpr()
    .flatMap { it.assertType<QExpr>() }
    .flatMap { it.content.assertCons() }
    .map(f)
    .merge()
