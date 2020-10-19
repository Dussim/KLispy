package asserts

import ErrorExpr
import Expr
import datastructures.Cons
import datastructures.L
import datastructures.index
import datastructures.size

inline fun <reified T> L<Any>.checkType(atIndex: Int, callback: (Expr) -> Unit) {
    val l = index(atIndex)
    val kClass = T::class
    if (l !is Cons) {
        callback(ErrorExpr("Expected element of type ${kClass.simpleName} at index $atIndex but there was no element at given index"))
    } else {
        val headKClass = l.head::class
        if (headKClass == kClass) {
            callback(ErrorExpr("Expected element of type ${kClass.simpleName} at index $atIndex but got ${headKClass.simpleName}"))
        }
    }
}

inline fun <T> L<T>.checkLength(expectedLength: Int, canBeLonger: Boolean = false, error: (Expr) -> Unit) {
    if (canBeLonger) {
        if (size <= expectedLength) error(ErrorExpr("Expected list of size at least $expectedLength but got list of size $size"))
    } else {
        if (size != expectedLength) error(ErrorExpr("Expected list of size $expectedLength but got list of size $size"))
    }
}

inline fun L<Expr>.exactly2(callback: (Expr, Expr) -> Expr): Expr {
    checkLength(2) { return it }
    val (a, tail) = this as Cons
    val (b, _) = tail as Cons
    return callback(a, b)
}
