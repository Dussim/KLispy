package datastructures

sealed interface Either<out L, out R> {
    companion object
}

data class Left<out L>(val left: L) : Either<L, Nothing> {
    operator fun invoke() = left
}

data class Right<out R>(val right: R) : Either<Nothing, R> {
    operator fun invoke() = right
}

fun <A, B> Either.Companion.right(right: B): Either<A, B> = Right(right)
fun <A, B> Either.Companion.left(left: A): Either<A, B> = Left(left)

inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = when (this) {
    is Left -> this
    is Right -> f(right)
}

inline fun <A, B, C> Either<A, B>.map(f: (B) -> C): Either<A, C> = flatMap { Either.right(f(it)) }
inline fun <A, B, C> Either<A, B>.mapLeft(f: (A) -> C): Either<C, B> = when (this) {
    is Left -> Left(f(left))
    is Right -> this
}

fun <A, B : A, C : A> Either<B, C>.merge(): A = when (this) {
    is Left -> left
    is Right -> right
}

fun <A, B, C> Either<A, B>.combine(o: Either<A, C>): Either<A, Pair<B, C>> {
    return flatMap { b -> o.map { c -> Pair(b, c) } }
}

fun <A, B, C, D> Either<A, B>.combine(q: Either<A, C>, w: Either<A, D>): Either<A, Triple<B, C, D>> {
    return flatMap { b -> q.flatMap { c -> w.map { d -> Triple(b, c, d) } } }
}
