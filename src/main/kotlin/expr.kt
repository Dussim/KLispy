import asserts.allOfType
import asserts.assertAtLeastOneExpr
import asserts.assertCons
import asserts.assertOneExpr
import asserts.assertThreeExprs
import asserts.assertTwoExprs
import asserts.assertType
import asserts.assertTypes
import datastructures.Cons
import datastructures.L
import datastructures.Left
import datastructures.Right
import datastructures.Some
import datastructures.find
import datastructures.flatMap
import datastructures.flatten
import datastructures.forEach
import datastructures.isEmpty
import datastructures.isOneElement
import datastructures.iterator
import datastructures.joinToString
import datastructures.map
import datastructures.merge
import datastructures.of
import datastructures.reduce
import datastructures.zip
import env.Env
import env.get
import env.set
import env.subEnv
import iterators.toL
import iterators.zip
import utils.bindRight

sealed class Expr {
    open fun eval(env: Env, l: L<Expr>): Expr = eval(env)
    open fun eval(env: Env): Expr = this
}

data class ErrorExpr(val reason: String) : Expr() {
    override fun eval(env: Env, l: L<Expr>): Expr = TODO()

    companion object
}

data class Number(val value: Double) : Expr() {
    override fun toString(): String = value.toString()

    operator fun plus(o: Number) = Number(value + o.value)
    operator fun minus(o: Number) = Number(value - o.value)
    operator fun times(o: Number) = Number(value * o.value)
    operator fun div(o: Number) = Number(value / o.value)

    operator fun compareTo(o: Number): Int {
        return value.compareTo(o.value)
    }

    override fun eval(env: Env, l: L<Expr>): Expr = TODO()
    override fun eval(env: Env): Expr = this
}

sealed class Symbol : Expr() {
    abstract val symbol: String

    data class Unbound(override val symbol: String) : Symbol() {
        override fun toString(): String = symbol
        override fun eval(env: Env): Expr = env[symbol] ?: ErrorExpr("Symbol [ $symbol ] not found")
        override fun eval(env: Env, l: L<Expr>): Expr = env[symbol]?.eval(env, l) ?: ErrorExpr("Symbol [ $symbol ] not found")
    }

    data class Builtin(override val symbol: String, val function: Symbol.(env: Env, l: L<Expr>) -> Expr) : Symbol() {
        override fun toString(): String = symbol
        override fun eval(env: Env): Expr = this
        override fun eval(env: Env, l: L<Expr>): Expr = function(env, l)
    }

    data class Bound(override val symbol: String, val expr: Expr) : Symbol() {
        override fun toString(): String = expr.toString()
        override fun eval(env: Env): Expr = expr
        override fun eval(env: Env, l: L<Expr>): Expr = expr.eval(env, l)
    }
}

data class QExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("{", "}")
}

data class Lambda(val symbols: L<Symbol.Unbound>, val body: QExpr) : Expr() {
    override fun eval(env: Env, l: L<Expr>): Expr {
        val innerEnv = env.subEnv()
        val symbolsIterator = symbols.iterator()
        val valuesIterator = l.iterator()

        symbolsIterator.zip(valuesIterator, ::Pair).forEach { (unbound, expr) ->
            innerEnv.set(unbound.bindTo(expr))
        }

        // currying
        return when (symbolsIterator.hasNext()) {
            true -> Lambda(symbolsIterator.toL(), body.deepConstitution(innerEnv) as QExpr)
            false -> SExpr(body.content).eval(innerEnv)
        }
    }

    override fun toString(): String = "\\ ${symbols.joinToString("{", "}")} $body"
}

data class SExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("(", ")")

    override fun eval(env: Env): Expr {
        if (content.isEmpty()) return SExpr()
        val evaluated = content.map(Expr::evalChildren bindRight env)
        return Right(Pair(evaluated, evaluated.find(error)))
            .flatMap { (list, error) -> if (error is Some) Left(error.value) else Right(list) }
            .flatMap(L<Expr>::assertAtLeastOneExpr)
            .map { (expr, l) ->
                if (l.isEmpty()) return expr
                when (expr) {
                    is Lambda, is Symbol -> expr.eval(env, l)
                    else -> ErrorExpr("Don't know what to do with ${evaluated.joinToString("[", "]")}")
                }
            }.merge()
    }

    companion object {
        private val error: (Expr) -> Boolean = { it is ErrorExpr }
    }
}

private fun Expr.evalChildren(env: Env): Expr = when (this) {
    is SExpr -> eval(env)
    is Symbol -> eval(env)
    else -> this
}

sealed class Bool(private val name: String) : Expr() {
    override fun toString(): String = name
}

object False : Bool("false")
object True : Bool("true")

fun mathOp(symbol: String, op: (Cons<Number>) -> Expr): Symbol.Builtin {
    return Symbol.Builtin(symbol) { _, l ->
        Right(l)
            .flatMap { it.allOfType<Number>() }
            .flatMap { it.assertCons() }
            .map(op)
            .merge()
    }
}

fun qExprFun(l: L<Expr>, f: (Cons<Expr>) -> Expr): Expr = l.assertOneExpr()
    .flatMap { it.assertType<QExpr>() }
    .flatMap { it.content.assertCons() }
    .map(f)
    .merge()

val plus = mathOp("+") { cons -> cons.reduce(Number::plus) }
val multiply = mathOp("*") { cons -> cons.reduce(Number::times) }
val divide = mathOp("/") { cons -> cons.reduce(Number::div) }
val minus = mathOp("-") { cons -> if (cons.isOneElement()) Number(-cons.head.value) else cons.reduce(Number::minus) }
val list = Symbol.Builtin("list") { _, l -> QExpr(l) }
val head = Symbol.Builtin("head") { _, l -> qExprFun(l) { cons -> QExpr(cons.head) } }
val tail = Symbol.Builtin("tail") { _, l -> qExprFun(l) { cons -> QExpr(cons.tail) } }
val eval = Symbol.Builtin("eval") { env, l -> qExprFun(l) { cons -> SExpr(cons).eval(env) } }
val join = Symbol.Builtin("join") { _, l ->
    Right(l)
        .flatMap { it.allOfType<QExpr>() }
        .flatMap { it.assertCons() }
        .map { it.map(QExpr::content) }
        .map { it.flatten() }
        .map { QExpr(it) }
        .merge()
}
val def = define("def", true)
val assign = define("=", false)
val lambda = Symbol.Builtin("\\") { _, l ->
    l.assertTwoExprs()
        .flatMap { (a, b) -> assertTypes<QExpr, QExpr>(a, b) }
        .map { (a, b) -> Pair(a.content, b) }
        .flatMap { (arguments, body) ->
            arguments.allOfType<Symbol.Unbound>()
                .map { symbols -> Lambda(symbols, body) }
        }.merge()
}

val `if` = Symbol.Builtin("if") { env, l ->
    l.assertThreeExprs()
        .flatMap { (a, b, c) -> assertTypes<Bool, QExpr, QExpr>(a, b, c) }
        .map { (a, b, c) -> Triple(a, b.toSExpr(), c.toSExpr()) }
        .map { (a, b, c) -> if (a is True) b else c }
        .map { it.eval(env) }
        .merge()
}

fun define(op: String, global: Boolean): Symbol.Builtin {
    return Symbol.Builtin(op) { env, l ->
        val setSymbol: (Pair<Symbol.Unbound, Expr>) -> Unit = { (symbol, expr) ->
            env.set(symbol.bindTo(expr), global)
        }
        l.assertCons()
            .flatMap { (a, b) ->
                a.assertType<QExpr>().map { qExpr -> Pair(qExpr, b) }
            }
            .flatMap { (a, b) ->
                a.content.allOfType<Symbol.Unbound>()
                    .map { symbols -> Pair(symbols, b) }
            }
            .map { (symbols, arguments) -> symbols.zip(arguments, ::Pair) }
            .map { it.forEach(setSymbol).run { SExpr() } }
            .merge()
    }
}

fun Symbol.Unbound.bindTo(expr: Expr): Symbol.Bound = Symbol.Bound(symbol, expr)

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

fun QExpr.toSExpr(): SExpr = SExpr(content)

