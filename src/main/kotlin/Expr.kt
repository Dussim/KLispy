import datastructures.Cons
import datastructures.L
import datastructures.None
import datastructures.Some
import datastructures.allInstanceOf
import datastructures.find
import datastructures.flatten
import datastructures.forEach
import datastructures.isOneElement
import datastructures.iterator
import datastructures.joinToString
import datastructures.map
import datastructures.of
import datastructures.reduce
import datastructures.size
import datastructures.zip
import env.Env
import env.get
import env.set
import env.subEnv
import iterators.toL
import iterators.zip
import utils.emptyParamListForSymbol
import utils.paramsNotNumbers

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

fun mathOp(symbol: String, op: (Cons<Number>) -> Expr): Symbol.Builtin {
    return Symbol.Builtin(symbol) { _, l ->
        when (l) {
            None -> ErrorExpr.emptyParamListForSymbol(this)
            is Cons -> when (l.allInstanceOf(Number::class.java)) {
                true -> op(l as Cons<Number>)
                false -> ErrorExpr.paramsNotNumbers(this, l)
            }
        }
    }
}

fun qExprFun(name: String, l: L<Expr>, f: (Cons<Expr>) -> Expr): Expr {
    return if (l is Cons && l.isOneElement() && l.head is QExpr && l.head.content is Cons) {
        f(l.head.content)
    } else {
        ErrorExpr(reason = "$name expects one parameter of type QExpr with at least one element but got $l")
    }
}

val plus = mathOp("+") { cons -> cons.reduce(Number::plus) }
val multiply = mathOp("*") { cons -> cons.reduce(Number::times) }
val divide = mathOp("/") { cons -> cons.reduce(Number::div) }
val minus = mathOp("-") { cons -> if (cons.isOneElement()) Number(-cons.head.value) else cons.reduce(Number::minus) }
val list = Symbol.Builtin("list") { _, l -> QExpr(l) }
val head = Symbol.Builtin("head") { _, l -> qExprFun("head", l) { cons -> QExpr(cons.head) } }
val tail = Symbol.Builtin("tail") { _, l -> qExprFun("tail", l) { cons -> QExpr(cons.tail) } }
val eval = Symbol.Builtin("eval") { env, l -> qExprFun("eval", l) { cons -> SExpr(cons).eval(env) } }
val join = Symbol.Builtin("join") { _, l ->
    when (l.allInstanceOf(QExpr::class.java)) {
        true -> (l as L<QExpr>).run { QExpr(map(QExpr::content).flatten()) }
        false -> ErrorExpr(reason = "join expects one or more parameters of type QExpr but got $l")
    }
}
val def = Symbol.Builtin("def") { env, l ->
    if (l is Cons && l.head is QExpr && l.head.content.allInstanceOf(Symbol.Unbound::class.java)) {
        val symbols = l.head.content as L<Symbol.Unbound>
        symbols.zip(l.tail, ::Pair).forEach { (runtimeSymbol, expr) ->
            env.set(runtimeSymbol.bindTo(expr), true)
        }
        SExpr()
    } else {
        ErrorExpr(reason = "def cannot define non-symbol in $l")
    }
}
val lambda = Symbol.Builtin("\\") { _, l ->
    if (
        l.size == 2 &&
        l is Cons &&
        l.head is QExpr &&
        l.tail is Cons &&
        l.tail.head is QExpr &&
        l.head.content.allInstanceOf(Symbol.Unbound::class.java)
    ) {
        val symbols = l.head.content as L<Symbol.Unbound>
        val body = l.tail.head
        Lambda(symbols, body)
    } else {
        ErrorExpr(reason = "TODO error message")
    }
}

data class SExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("(", ")")

    // todo refactor this, it looks terrible
    override fun eval(env: Env): Expr = when (content) {
        None -> this
        is Cons -> {
            val evaluated = content.map { expr ->
                when (expr) {
                    is SExpr -> expr.eval(env)
                    is Symbol -> expr.eval(env)
                    else -> expr
                }
            }
            when (val error = evaluated.find(error)) {
                is Some -> error.value
                else -> {
                    if (evaluated.isOneElement()) {
                        evaluated.head
                    } else {
                        val (head, tail) = evaluated
                        when (head) {
                            is Lambda, is Symbol -> head.eval(env, tail)
                            else -> ErrorExpr("Don't know what to do with ${evaluated.joinToString("[", "]")}")
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val error: (Expr) -> Boolean = { it is ErrorExpr }
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
