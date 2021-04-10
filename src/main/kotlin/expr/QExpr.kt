package expr

import datastructures.L
import datastructures.joinToString
import datastructures.of
import env.Env

data class QExpr(val content: L<Expr>) : Expr {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("{", "}")

    override fun eval(env: Env): Expr = this
    override fun eval(env: Env, l: L<Expr>): Expr = cantApply(env, parameters = l, to = "QExpr${content.joinToString("(", ")")}")
}

fun QExpr.toSExpr(): SExpr = SExpr(content)