package iterators

import datastructures.Cons
import datastructures.L
import datastructures.None
import datastructures.isNotEmpty

class ListIter<T>(
    private var current: L<T>
) : Iter<T> {
    override fun hasNext(): Boolean = current.isNotEmpty()

    override fun next(): T {
        return when (val c = current) {
            None -> throw IllegalStateException("Iterator is empty")
            is Cons -> {
                current = c.tail
                c.head
            }
        }
    }
}
