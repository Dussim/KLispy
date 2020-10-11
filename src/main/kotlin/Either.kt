sealed class Either<out L, out R>

data class Left<out L>(val left: L) : Either<L, Nothing>() {
    operator fun invoke() = left
}

data class Right<out R>(val right: R) : Either<Nothing, R>() {
    operator fun invoke() = right
}
