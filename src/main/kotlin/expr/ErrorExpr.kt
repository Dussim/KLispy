package expr

import datastructures.L
import env.Env

data class ErrorExpr(val reason: String) : Expr {
    override fun toString(): String = "Error: $reason"

    override fun eval(env: Env, l: L<Expr>): Expr = cantApply(env, parameters = l, to = "ErrorExpr(${toString()}")

    override fun eval(env: Env): Expr = this
}
