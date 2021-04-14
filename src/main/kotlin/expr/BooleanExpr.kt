package expr

import datastructures.L
import env.Env

sealed class BooleanExpr(private val name: String) : Expr {
    override fun toString(): String = name

    override fun eval(env: Env, l: L<Expr>): Expr = cantApply(env, parameters = l, to = "BooleanExpr($name)")

    override fun eval(env: Env): Expr = this

    override fun eq(o: Expr): Boolean {
        return o == this
    }
}

object False : BooleanExpr("false")
object True : BooleanExpr("true")
