sealed class O<out T>
object Empty : O<Nothing>()
data class Some<T>(val value: T) : O<T>()
