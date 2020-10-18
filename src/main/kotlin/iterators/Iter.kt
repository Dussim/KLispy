package iterators

import datastructures.Cons
import datastructures.L
import datastructures.None
import datastructures.reverse

interface Iter<T> {
    fun hasNext(): Boolean
    fun next(): T
}

fun <T, V, R> Iter<T>.zip(o: Iter<V>, op: (T, V) -> R): L<R> = when {
    hasNext() && o.hasNext() -> Cons(op(next(), o.next()), zip(o, op))
    else -> None
}

fun <T> Iter<T>.toL(): L<T> = when (hasNext()) {
    true -> Cons(next(), toL())
    false -> None
}.reverse()
