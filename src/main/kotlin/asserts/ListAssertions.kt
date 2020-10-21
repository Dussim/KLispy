package asserts

import ErrorExpr
import Expr
import datastructures.Cons
import datastructures.Either
import datastructures.L
import datastructures.Left
import datastructures.None
import datastructures.Right
import datastructures.empty
import datastructures.flatMap
import datastructures.forEach
import datastructures.map
import datastructures.right
import datastructures.size

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Expr> L<Expr>.allOfType(): Either<ErrorExpr, L<T>> = when (this) {
    None -> Right(this as L<T>)
    is Cons -> {
        var current = Either.right<ErrorExpr, L<T>>(L.empty())
        map { it.assertType<T>() }.forEach { element ->
            current = current.flatMap { list ->
                element.map { Cons(it, list) }
            }
        }
        current
    }
}

fun <T> L<T>.assertLength(expectedLength: Int, canBeLonger: Boolean = false): Either<ErrorExpr, L<T>> {
    return if (canBeLonger) {
        if (size >= expectedLength) {
            Right(this)
        } else {
            ErrorExpr("Expected list of size at least $expectedLength but got list of size $size").let(::Left)
        }
    } else {
        if (size == expectedLength) {
            Right(this)
        } else {
            ErrorExpr("Expected list of size $expectedLength but got list of size $size").let(::Left)
        }
    }
}

fun L<Expr>.assertOneExpr(): Either<ErrorExpr, Expr> {
    return assertLength(1)
        .map { l -> (l as Cons).head }
}

fun L<Expr>.assertTwoExprs(): Either<ErrorExpr, Pair<Expr, Expr>> {
    return assertLength(2)
        .map { l ->
            val (a, tail) = l as Cons
            val (b, _) = tail as Cons
            Pair(a, b)
        }
}

fun L<Expr>.assertThreeExprs(): Either<ErrorExpr, Triple<Expr, Expr, Expr>> {
    return assertLength(3)
        .map { l ->
            val (a, t1) = l as Cons
            val (b, t2) = t1 as Cons
            val (c, _) = t2 as Cons
            Triple(a, b, c)
        }
}

fun <T> L<T>.assertCons(): Either<ErrorExpr, Cons<T>> {
    return assertLength(1, canBeLonger = true)
        .map { l -> (l as Cons) }
}

fun L<Expr>.assertAtLeastOneExpr(): Either<ErrorExpr, Pair<Expr, L<Expr>>> {
    return assertLength(1, canBeLonger = true)
        .flatMap(L<Expr>::assertCons)
        .map { (head, tail) -> Pair(head, tail) }
}
