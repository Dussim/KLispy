package expr

import datastructures.L
import env.Env

data class NumberExpr(val value: Double) : Expr {
    override fun toString(): String = value.toString()

    operator fun plus(o: NumberExpr) = NumberExpr(value + o.value)
    operator fun minus(o: NumberExpr) = NumberExpr(value - o.value)
    operator fun times(o: NumberExpr) = NumberExpr(value * o.value)
    operator fun div(o: NumberExpr) = NumberExpr(value / o.value)

    operator fun compareTo(o: NumberExpr): Int {
        return value.compareTo(o.value)
    }

    override fun eval(env: Env): Expr = this
    override fun eval(env: Env, l: L<Expr>): Expr = cantApply(env, parameters = l, to = "Number($value)")
}
