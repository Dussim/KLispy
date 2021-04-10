package expr

import asserts.assertAtLeastOneExpr
import datastructures.L
import datastructures.Left
import datastructures.Right
import datastructures.Some
import datastructures.find
import datastructures.flatMap
import datastructures.isEmpty
import datastructures.joinToString
import datastructures.map
import datastructures.merge
import datastructures.of
import env.Env
import utils.bindRight

data class SExpr(val content: L<Expr>) : Expr {
    constructor(vararg expr: Expr) : this(L.of(*expr))

    override fun toString(): String = content.joinToString("(", ")")

    override fun eval(env: Env): Expr {
        if (content.isEmpty()) return SExpr()
        val evaluated = content.map(Expr::evalChildren bindRight env)
        return Right(Pair(evaluated, evaluated.find(error)))
            .flatMap { (list, error) -> if (error is Some) Left(error.value) else Right(list) }
            .flatMap(L<Expr>::assertAtLeastOneExpr)
            .map { (expr, l) -> expr.eval(env, l) }
            .merge()
    }

    override fun eval(env: Env, l: L<Expr>): Expr = cantApply(env, parameters = l, to = "SExpr${content.joinToString("(", ")")}")

    companion object {
        private val error: (Expr) -> Boolean = { it is ErrorExpr }
    }
}

private fun Expr.evalChildren(env: Env): Expr = when (this) {
    is SExpr -> eval(env)
    is SymbolExpr -> eval(env)
    else -> this
}
