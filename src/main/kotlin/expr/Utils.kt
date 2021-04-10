package expr

import datastructures.L
import datastructures.isEmpty
import datastructures.joinToString
import env.Env

fun Expr.cantApply(env: Env, parameters: L<Expr>, to: String): Expr = when (parameters.isEmpty()) {
    true -> eval(env)
    false -> ErrorExpr("Can't apply parameters ${parameters.joinToString("[", "]")} to $to")
}
