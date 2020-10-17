package datastructures

import iterators.Iter
import iterators.ListIter

sealed class L<out T> {
    companion object
}

object None : L<Nothing>() {
    override fun toString() = "None"
}

data class Cons<out T>(val head: T, val tail: L<T> = None) : L<T>()

val <T> L<T>.size: Int
    get() = when (this) {
        None -> 0
        is Cons -> 1 + tail.size
    }

fun <T> L.Companion.of(vararg values: T): L<T> = list(values.iterator())
fun <T> L.Companion.empty() = None as L<T>

private fun <T> list(iterator: Iterator<T>): L<T> = when (iterator.hasNext()) {
    false -> None
    true -> Cons(iterator.next(), list(iterator))
}

fun <T> L<T>.all(predicate: (T) -> Boolean): Boolean = when (this) {
    None -> true
    is Cons -> predicate(head) && tail.all(predicate)
}

fun <T> L<T>.any(predicate: (T) -> Boolean): Boolean = when (this) {
    None -> false
    is Cons -> predicate(head) || tail.any(predicate)
}

fun <T, V> L<T>.map(mapping: (T) -> V): L<V> = when (this) {
    None -> None
    is Cons -> Cons(mapping(head), tail.map(mapping))
}

fun <T, V> Cons<T>.map(mapping: (T) -> V) = Cons(mapping(head), tail.map(mapping))

fun <T, V> L<T>.fold(acc: V, op: (V, T) -> V): V = when (this) {
    None -> acc
    is Cons -> tail.fold(op(acc, head), op)
}

fun <T> Cons<T>.reduce(op: (T, T) -> T): T = when (tail) {
    None -> head
    is Cons -> tail.fold(head, op)
}

fun <T> L<*>.allInstanceOf(clazz: Class<T>): Boolean = all { clazz.isInstance(it) }

fun <T> L<T>.find(predicate: (T) -> Boolean): O<T> = when (this) {
    None -> Empty
    is Cons -> when (predicate(head)) {
        true -> Some(head)
        false -> tail.find(predicate)
    }
}

fun <T> L<T>.isEmpty(): Boolean = this is None

fun <T> L<T>.isNotEmpty(): Boolean = this is Cons

fun <T> L<T>.isOneElement(): Boolean = size == 1

fun <T> L<T>.forEach(op: (T) -> Unit) {
    if (this is Cons) {
        op(head)
        tail.forEach(op)
    }
}

fun <T> L<L<T>>.flatten(): L<T> = when (this) {
    None -> None
    is Cons -> fold<L<T>, L<T>>(L.empty()) { acc, list ->
        list.fold(acc) { acc2, elem -> Cons(elem, acc2) }
    }.reverse()
}

fun <T> L<T>.reverse(): L<T> = when (this) {
    None -> None
    is Cons -> fold(L.empty()) { acc, elem -> Cons(elem, acc) }
}

fun <T> L<T>.joinToString(
    prefix: String = "",
    suffix: String = "",
    separator: String = "",
    transform: (T) -> String = { it.toString() }
): String = when (this) {
    None -> "$prefix $suffix"
    is Cons -> "$prefix ${transform(head)}$separator ${tail.joinToStringInternal(prefix, suffix, separator, transform)}"
}

private fun <T> L<T>.joinToStringInternal(
    prefix: String = "",
    suffix: String = "",
    separator: String = "",
    transform: (T) -> String
): String = when (this) {
    None -> suffix
    is Cons -> "${transform(head)}$separator ${tail.joinToStringInternal(prefix, suffix, transform = transform)}"
}

fun <T, V, R> L<T>.zip(o: L<V>, op: (T, V) -> R): L<R> = when {
    this is Cons && o is Cons -> Cons(op(head, o.head), tail.zip(o.tail, op))
    else -> None
}

fun <T> L<T>.iterator(): Iter<T> = ListIter(this)
