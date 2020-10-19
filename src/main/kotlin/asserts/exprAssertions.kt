package asserts

import ErrorExpr
import Expr

inline fun <reified T : Expr> Expr.checkType(f: (T) -> Expr): Expr {
    return if (this !is T) {
        ErrorExpr("Expected type ${T::class.simpleName} but got ${this::class.simpleName}")
    } else {
        f(this)
    }
}

inline fun <reified T : Expr, reified V : Expr> checkTypes(a: Expr, b: Expr, f: (T, V) -> Expr): Expr {
    return a.checkType<T> { t -> b.checkType<V> { v -> f(t, v) } }
}
