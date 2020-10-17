package iterators

interface Iter<T> {
    fun hasNext(): Boolean
    fun next(): T
}
