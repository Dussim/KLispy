package expr

import datastructures.L
import env.Env

sealed interface Expr {
    fun eval(env: Env, l: L<Expr>): Expr
    fun eval(env: Env): Expr

    fun eq(o: Expr): Boolean
}
