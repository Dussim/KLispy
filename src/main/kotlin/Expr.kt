sealed class Expr : ParameterlessEvaluation<Expr>, ParameterEvaluation<Expr> {
    override fun eval(l: L<Expr>): Expr = eval()
    override fun eval(): Expr = this
}

data class Error(val reason: String) : Expr() {
    override fun eval(l: L<Expr>): Expr = TODO()

    companion object
}

data class Number(val value: Double) : Expr() {
    override fun toString(): String = value.toString()

    operator fun plus(o: Number) = Number(value + o.value)
    operator fun minus(o: Number) = Number(value - o.value)
    operator fun times(o: Number) = Number(value * o.value)
    operator fun div(o: Number) = Number(value / o.value)

    override fun eval(l: L<Expr>): Expr = TODO()
    override fun eval(): Expr = this
}

sealed class BuiltInSymbol(val symbol: String, private val op: (Number, Number) -> Number) : Expr() {
    override fun toString(): String = symbol
    override fun eval(): Expr = this

    @Suppress("UNCHECKED_CAST")
    override fun eval(l: L<Expr>): Expr = when (l) {
        None -> Error.emptyParamListForSymbol(this)
        is Cons -> when (l.allInstanceOf(Number::class.java)) {
            true -> {
                l as Cons<Number>
                when (this is Minus && l.isOneElement()) {
                    true -> Number(-l.head.value)
                    false -> l.reduce(op)
                }
            }
            false -> Error.paramsNotNumbers(this, l)
        }
    }
}

object Plus : BuiltInSymbol("+", Number::plus)
object Minus : BuiltInSymbol("-", Number::minus)
object Multiply : BuiltInSymbol("*", Number::times)
object Divide : BuiltInSymbol("/", Number::div)

sealed class BuiltInFunction(val name: String) : Expr() {
    override fun toString() = name
    override fun eval(): Expr = this
}

object Head : BuiltInFunction("head") {
    override fun eval(l: L<Expr>): Expr = qExprFun(name, l) { cons -> QExpr(cons.head) }
}

object Tail : BuiltInFunction("tail") {
    override fun eval(l: L<Expr>): Expr = qExprFun(name, l) { cons -> QExpr(cons.tail) }
}

object Eval : BuiltInFunction("eval") {
    override fun eval(l: L<Expr>): Expr = qExprFun(name, l) { cons -> SExpr(cons).eval() }
}

object ListF : BuiltInFunction("list") {
    override fun eval(l: L<Expr>): Expr = QExpr(l)
}

object Join : BuiltInFunction("join") {
    @Suppress("UNCHECKED_CAST")
    override fun eval(l: L<Expr>): Expr = when (l.allInstanceOf(QExpr::class.java)) {
        true -> {
            l as L<QExpr>
            QExpr(l.map(QExpr::content).flatten())
        }
        false -> Error(reason = "$name expects one or more parameters of type QExpr but got $l")
    }
}

fun qExprFun(name: String, l: L<Expr>, f: (Cons<Expr>) -> Expr): Expr {
    return if (l is Cons && l.isOneElement() && l.head is QExpr && l.head.content is Cons) {
        f(l.head.content)
    } else {
        Error(reason = "$name expects one parameter of type QExpr with at least one element but got $l")
    }
}

data class SExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("(", ")")

    override fun eval() = when (content) {
        None -> this
        is Cons -> {
            val evaluated = content.map(evalChildren)
            when (val error = evaluated.find(error)) {
                is Some -> error.value
            }
            if (evaluated.isOneElement()) {
                evaluated.head
            } else {
                val (head, tail) = evaluated
                when (head) {
                    is BuiltInSymbol -> head.eval(tail)
                    is BuiltInFunction -> head.eval(tail)
                    else -> Error(reason = "Not a symbol")
                }
            }
        }
    }

    private val evalChildren: (Expr) -> Expr = { expr ->
        when (expr) {
            is SExpr -> expr.eval()
            else -> expr
        }
    }

    private val error: (Expr) -> Boolean = { it is Error }
}

data class QExpr(val content: L<Expr>) : Expr() {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("{", "}")
}

fun interface ParameterlessEvaluation<T : ParameterlessEvaluation<T>> {
    fun eval(): T
}

fun interface ParameterEvaluation<T : ParameterEvaluation<T>> {
    fun eval(l: L<T>): T
}
