import datastructures.Cons
import datastructures.L
import datastructures.None
import datastructures.Some
import datastructures.allInstanceOf
import datastructures.find
import datastructures.flatten
import datastructures.forEach
import datastructures.isOneElement
import datastructures.joinToString
import datastructures.map
import datastructures.of
import datastructures.reduce
import env.EmptyEnv
import env.Env
import utils.emptyParamListForSymbol
import utils.paramsNotNumbers

sealed class Expr : ParameterlessEvaluation<Expr>, ParameterEvaluation<Expr> {
    override fun eval(env: Env, l: L<Expr>): Expr = eval(env)
    override fun eval(env: Env): Expr = this
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

sealed class BuiltInSymbol(val symbol: String, private val op: (Number, Number) -> Number) : Expr() {
    override fun toString(): String = symbol
    override fun eval(env: Env): Expr = this

    @Suppress("UNCHECKED_CAST")
    override fun eval(env: Env, l: L<Expr>): Expr = when (l) {
        None -> ErrorExpr.emptyParamListForSymbol(this)
        is Cons -> when (l.allInstanceOf(Number::class.java)) {
            true -> {
                l as Cons<Number>
                when (this is Minus && l.isOneElement()) {
                    true -> Number(-l.head.value)
                    false -> l.reduce(op)
                }
            }
            false -> ErrorExpr.paramsNotNumbers(this, l)
        }
    }
}

object Plus : BuiltInSymbol("+", Number::plus)
object Minus : BuiltInSymbol("-", Number::minus)
object Multiply : BuiltInSymbol("*", Number::times)
object Divide : BuiltInSymbol("/", Number::div)

sealed class BuiltInFunction(val name: String) : Expr() {
    override fun toString() = name
    override fun eval(env: Env): Expr = this
}

object Head : BuiltInFunction("head") {
    override fun eval(env: Env, l: L<Expr>): Expr = qExprFun(name, l) { cons -> QExpr(cons.head) }
}

object Tail : BuiltInFunction("tail") {
    override fun eval(env: Env, l: L<Expr>): Expr = qExprFun(name, l) { cons -> QExpr(cons.tail) }
}

object Eval : BuiltInFunction("eval") {
    override fun eval(env: Env, l: L<Expr>): Expr = qExprFun(name, l) { cons -> SExpr(cons).eval(env) }
}

object ListF : BuiltInFunction("list") {
    override fun eval(env: Env, l: L<Expr>): Expr = QExpr(l)
}

object Join : BuiltInFunction("join") {
    @Suppress("UNCHECKED_CAST")
    override fun eval(env: Env, l: L<Expr>): Expr = when (l.allInstanceOf(QExpr::class.java)) {
        true -> {
            l as L<QExpr>
            QExpr(l.map(QExpr::content).flatten())
        }
        false -> ErrorExpr(reason = "$name expects one or more parameters of type QExpr but got $l")
    }
}

object Def : BuiltInFunction("def") {
    override fun eval(env: Env, l: L<Expr>): Expr {
        if (l is Cons && l.head is QExpr && l.head.content.allInstanceOf(RuntimeSymbol::class.java)) {
            val symbols = l.head.content as L<RuntimeSymbol>
            var value = l.tail as Cons<Expr>
            symbols.forEach { symbol ->
                env.add(symbol, value.head)
                if (value.tail is Cons<Expr>) {
                    value = value.tail as Cons<Expr>
                }
            }
            return SExpr()
        }
        return ErrorExpr(reason = "$name cannot define non-symbol in $l")
    }
}

fun qExprFun(name: String, l: L<Expr>, f: (Cons<Expr>) -> Expr): Expr {
    return if (l is Cons && l.isOneElement() && l.head is QExpr && l.head.content is Cons) {
        f(l.head.content)
    } else {
        ErrorExpr(reason = "$name expects one parameter of type QExpr with at least one element but got $l")
    }
}

data class SExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("(", ")")

    override fun eval(env: Env): Expr = when (content) {
        None -> this
        is Cons -> {
            val evaluated = content.map { expr ->
                when (expr) {
                    is SExpr -> expr.eval(env)
                    is RuntimeSymbol -> env[expr] ?: ErrorExpr(reason = "Symbol not found")
                    else -> expr
                }
            }
            when (val error = evaluated.find(error)) {
                is Some -> error.value
            }
            if (evaluated.isOneElement()) {
                evaluated.head
            } else {
                val (head, tail) = evaluated
                when (head) {
                    is BuiltInSymbol -> head.eval(env, tail)
                    is BuiltInFunction -> head.eval(env, tail)
                    is RuntimeSymbol -> env[head]?.eval(env, tail) ?: ErrorExpr("Unknown Symbol")
                    else -> ErrorExpr(reason = "Not a symbol")
                }
            }
        }
    }

    private val error: (Expr) -> Boolean = { it is ErrorExpr }
}

data class QExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("{", "}")
}

data class RuntimeSymbol(val name: String) : Expr()

fun interface ParameterlessEvaluation<T : ParameterlessEvaluation<T>> {
    fun eval(env: Env): T
}

fun interface ParameterEvaluation<T : ParameterEvaluation<T>> {
    fun eval(env: Env, l: L<T>): T
}

fun <T : ParameterlessEvaluation<T>> ParameterlessEvaluation<T>.evalEmptyEnv() = eval(EmptyEnv)
fun <T : ParameterEvaluation<T>> ParameterEvaluation<T>.evalEmptyEnv(l: L<T>) = eval(EmptyEnv, l)
