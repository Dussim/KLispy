package expr

import datastructures.L
import env.Env

class StringExpr(private val content: String) : Expr {
    override fun toString(): String = "'$content'"

    override fun eval(env: Env, l: L<Expr>): Expr = this

    override fun eval(env: Env): Expr = this

    override fun eq(o: Expr): Boolean {
        return (o as? StringExpr)?.content == content
    }
}
