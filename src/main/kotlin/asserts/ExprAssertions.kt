package asserts

import ErrorExpr
import Expr
import datastructures.Either
import datastructures.L
import datastructures.Left
import datastructures.Right
import datastructures.combine
import datastructures.filter
import datastructures.joinToString
import datastructures.map
import datastructures.mapLeft
import datastructures.of
import typeName

fun <T> Either<ErrorExpr, T>.mapErrorMessage(f: (String) -> String): Either<ErrorExpr, T> {
    return mapLeft { it.reason }
        .mapLeft { ErrorExpr(f(it)) }
}

fun L<Either<ErrorExpr, *>>.combineErrorMessages(): ErrorExpr {
    return filter { it is Left }
        .map { (it as Left)().reason }
        .joinToString(prefix = "\n", suffix = "\n", separator = "\n")
        .let(::ErrorExpr)
}

inline fun <reified A : Expr> Expr.assertType(): Either<ErrorExpr, A> = when (this is A) {
    true -> Right(this)
    false -> Left(ErrorExpr("Expected type ${A::class.simpleName} but got $typeName"))
}

fun <A> Either<ErrorExpr, A>.addPositionInfo(position: Int): Either<ErrorExpr, A> {
    return mapErrorMessage { "At position $position $it" }
}

inline fun <reified A : Expr, reified B : Expr> assertTypes(a: Expr, b: Expr): Either<ErrorExpr, Pair<A, B>> {
    val eitherA = a.assertType<A>().addPositionInfo(1)
    val eitherB = b.assertType<B>().addPositionInfo(2)

    return eitherA.combine(eitherB)
        .mapLeft { L.of(eitherA, eitherB).combineErrorMessages() }
}

inline fun <reified A : Expr, reified B : Expr, reified C : Expr> assertTypes(a: Expr, b: Expr, c: Expr): Either<ErrorExpr, Triple<A, B, C>> {
    val eitherA = a.assertType<A>().addPositionInfo(1)
    val eitherB = b.assertType<B>().addPositionInfo(2)
    val eitherC = c.assertType<C>().addPositionInfo(3)

    return eitherA.combine(eitherB, eitherC)
        .mapLeft { L.of(eitherA, eitherB, eitherC).combineErrorMessages() }
}
